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
				icon: ''
			}
		}
	});

})();