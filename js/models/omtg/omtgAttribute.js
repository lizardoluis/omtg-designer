var app = app || {};

(function() {
	'use strict';

	// OMTGAttribute Model
	// ----------

	app.OMTGAttribute = Backbone.Model.extend({
		defaults : function() {
			return {
				name : '',
				type : '',
				defaultValue : '',
				isKey : false,
				length : '',
				scale : '',
				size : '',
				isNotNull : false,
				domain : ''
			}
		}
	});

})();