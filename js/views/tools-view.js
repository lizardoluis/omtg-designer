(function($) {
	'use strict';
	
	app.ToolsView = Backbone.View.extend({

		render : function() {

			this.$el.empty();

			this.model.each(function(tool) {
				var toolView = new app.ToolView({
					model : tool
				});
				this.$el.append(toolView.render().el);
			}, this);

			return this;
		}
	});
})(jQuery);