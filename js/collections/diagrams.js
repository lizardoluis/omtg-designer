var app = app || {};

(function() {
	'use strict';

	var Diagrams = Backbone.Collection.extend({
		model : app.Diagram
	});
	
	// Global list of diagrams
	app.diagrams = new Diagrams();

})();
	