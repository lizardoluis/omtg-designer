(function() {
	'use strict';

	// OMTGDiagrams Collection
	// ----------
	
	app.omtg.Diagrams = Backbone.Collection.extend({
		model : app.omtg.Diagram,
				
		initialize: function() {	       
	        this.listenTo(this, 'change:selected', this.propagate_selected);
	    },
	    
	    removeAll : function() {
	    	var diagram;
	    	while (diagram = this.last()) {
	    		diagram.trigger("destroy", diagram);  
	    	}
		},
		
		removeSet : function(set){			
			if (confirm(app.msgs.DELETE_DIAGRAMS)){			
				for (var i = 0; i < set.length; i++){
					app.plumb.detachAllConnections(set[i].id, {fireEvent : false}); 
		    		set[i].trigger("destroy", set[i]);
		    	}
				app.canvasView.updateHistory();
			}
		},
		
		getSelected : function() {
			return this.where({selected : true});
		},
	    
	    propagate_selected: function(p) { 
    	
	    	if(!p.get('selected'))
	            return;
	        this.each(function(m) {
	            if(p.id != m.id)
	                m.set({ selected: false }, { silent: false });
	        });
	    }, 
	    
	    unselectAll: function(){ 
	    	this.each(function(m) {
	    		m.set({ selected: false }, { silent: false });
	    	});
	    },
		
	    getAttrById : function(id, attr) {		    	
			var diagram = this.findWhere({id : id});
			if(diagram)
				return diagram.get(attr);			
			return null;
		},
		
		findByName : function(name) {
			var ds = this.where({name : name});
			for(var i=0; i<ds.length; i++){
				return ds[i];
			}
			return null;
		},
		
		toXML: function() {
		
			var xml = "";
			this.each(function(model) {
	    		xml += model.toXML();
	    	});
			return "<classes>" + xml + "</classes>";
		},
	});

})();
	