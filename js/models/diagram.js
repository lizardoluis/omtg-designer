var app = app || {};

(function() {
	'use strict';

	app.Diagram = Backbone.Model.extend({
		defaults : function() {
			return {
				id : this.cid,
				type : '',
				name : '',
				attributes : new Attributes()
			}
		}
	});

})();