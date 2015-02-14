(function() {
	'use strict';

	// OMTGDiagrams Collection
	// ----------
	
	app.omtg.Diagrams = Backbone.Collection.extend({
		model : app.omtg.Diagram,
		
		initialize: function() {
	        this.on('change:selected', this.propagate_selected);
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
		
	    get : function(id, attr) {			
			var diagram = this.findWhere({id : id});
			if(diagram)
				return diagram.get(attr);			
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
	