'use strict';

var Tools = Backbone.Collection.extend({
	model : Tool,
	
	// Filter down the list of all todo items that are finished.
	getTooltip: function (name) {
		return this.findWhere({name: name}).get('tooltip');
	},
	
	getName: function(tooltip) {
		return this.findWwhere({tooltip: tooltip}).get('name');
	}
});

