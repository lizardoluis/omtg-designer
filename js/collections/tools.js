(function() {
	'use strict';

	// Tools Collection
	// ----------

	app.Tools = Backbone.Collection.extend({
		model : app.Tool,
		
		initialize : function() {
			this.on('change:active', this.ensureOneActive);
		},

		activated : function() {
			return this.findWhere({
				active : true
			})
		},

		getTooltip : function(name) {
			return this.findWhere({
				name : name
			}).get('tooltip');
		},
		
		ensureOneActive : function(changed) {
			this.each(function(tool) {
				if(tool != changed)
					tool.set({active : tool === changed}, {silent: false});
			});
		}
	});

})();
