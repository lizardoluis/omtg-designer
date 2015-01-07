var app = app || {};

(function($) {
	'use strict';
	
	app.AppView = Backbone.View.extend({

		initialize : function() {
			this.toolboxesView = new app.ToolboxesView({model : toolboxes}).render();			
		}
	});
	
})(jQuery);