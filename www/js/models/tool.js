(function() {
	'use strict';

	// Tool Model
	// ----------

	app.Tool = Backbone.Model.extend({
		defaults : function() {
			return {
				name : '',
				type : '',
				tooltip: '',
				icon: '',
				active: false,
			};
		},
		
		// Toggle the `selected` state of this tool
		toggleActive: function () {
			this.set('active', !this.get('active'));
		},
	});

})();