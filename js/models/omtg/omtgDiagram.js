var app = app || {};

(function() {
	'use strict';
	
	// Diagram Model
	// ----------

	app.OMTGDiagram = Backbone.Model.extend({
		defaults : function() {
			return {
				id : this.cid,
				type : '',
				name : '',
				attributes : new app.OMTGAttributes(),
				selected : false
			}
		},
		
		// Toggle the `selected` state of this diagram.
		toggleSelected: function () {
			this.set('selected', !this.get('selected'));
		},
	});
	
})();