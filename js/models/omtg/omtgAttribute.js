var app = app || {};

(function() {
	'use strict';

	// OMTGAttribute Model
	// ----------

	app.OMTGAttribute = Backbone.Model.extend({
		defaults : function() {
			return {
				name : '',
				type : 'Varchar',
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