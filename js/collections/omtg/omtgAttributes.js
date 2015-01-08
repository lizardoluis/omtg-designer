var app = app || {};

(function() {
	'use strict';

	// OMTGDiagrams Collection
	// ----------
	
	app.OMTGAttributes = Backbone.Collection.extend({
		model : app.OMTGAttribute
	});
	
})();
	