(function() {
	'use strict';
	
	// Diagram Model
	// ----------

	app.omtg.Diagram = Backbone.Model.extend({
		defaults : function() {
			return {
				id : this.cid,
				type : '',
				name : '',
				attributes : new app.omtg.Attributes(),
				selected : false,
				top : 10,
				left : 10,
			}
		},
		
		// Toggle the `selected` state of this diagram.
		toggleSelected: function () {
			this.set('selected', !this.get('selected'));
		},
	});
	
})();