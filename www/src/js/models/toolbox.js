(function() {
	'use strict';

	// Toolbox Model
	// ----------

	app.Toolbox = Backbone.Model.extend({
		defaults : function() {
			return {
				name : '',
				tools : null
			}
		}
	});

})();