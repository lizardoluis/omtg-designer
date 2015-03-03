(function() {
	'use strict';

	// Tools Collection
	// ----------

	app.Tools = Backbone.Collection.extend({
		model : app.Tool,
		
		initialize : function() {
			this.listenTo(this, 'change:active', this.propagate_active);
		},
		
		propagate_active: function(p) {
	    	if(!p.get('active'))
	            return;
	        this.each(function(m) {
	            if(p.get('name') != m.get('name'))
	            	m.set({ active: false }, { silent: false });
	        });
	    },
	    
	    deactivateAll: function(){
	    	this.each(function(m) {
	    		m.set({ active: false }, { silent: false });
	    	});
	    },

		activated : function() {
			return this.findWhere({
				active : true
			});
		},

		getTooltip : function(name) {
			return this.findWhere({
				name : name
			}).get('tooltip');
		},
	});

})();
