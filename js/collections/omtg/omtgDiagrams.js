var app = app || {};

(function() {
	'use strict';

	// OMTGDiagrams Collection
	// ----------
	
	var OMTGDiagrams = Backbone.Collection.extend({
		model : app.OMTGDiagram
	});
	
	// Global list of diagrams
	app.diagrams = new OMTGDiagrams();

})();
	