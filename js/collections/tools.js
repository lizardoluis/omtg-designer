(function() {
	'use strict';

	// Tools Collection
	// ----------
	
	app.Tools = Backbone.Collection.extend({
		model : app.Tool,
		
		// Filter down the list of all todo items that are finished.
		getTooltip: function (name) {
			return this.findWhere({name: name}).get('tooltip');
		},
	});
	
})();
	