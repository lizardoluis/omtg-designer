(function() {
	'use strict';

	// Tool Model
	// ----------

	app.Canvas = Backbone.Model.extend({
		defaults : function() {
			return {
				diagrams : new app.omtg.Diagrams(),
				activeTool : null,
			}
		}
	});

})();