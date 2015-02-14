(function($) {
	'use strict';
	
	// Toolboxes View
	// ----------
	
	app.ToolboxesView = Backbone.View.extend({
				
		initialize : function() {
			this.render();
		},

		render : function() {
			this.$el.empty();

			this.model.each(function(toolbox) {
				var toolboxView = new app.ToolboxView({
					model : toolbox
				});
				this.$el.append(toolboxView.render().el);
			}, this);

		}
	});
})(jQuery);