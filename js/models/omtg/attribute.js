(function() {
	'use strict';

	// OMTGAttribute Model
	// ----------

	app.omtg.Attribute = Backbone.Model.extend({
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