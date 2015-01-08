var app = app || {};

(function() {
	'use strict';
	
	// Diagram Model
	// ----------

	app.OMTGDiagram = Backbone.Model.extend({
		defaults : function() {
			return {
				id : this.cid,
				type : '',
				name : '',
				attributes : new app.OMTGAttributes()
			}
		}
	});

})();