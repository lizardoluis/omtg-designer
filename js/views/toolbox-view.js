(function($) {
	'use strict';
	
	// Toolbox View
	// ----------
	
	app.ToolboxView = Backbone.View.extend({

		tagName : 'div',

		className : 'widget',

		initialize : function() {
						
			this.template = _.template($('#toolbox-template').html());
		},

		render : function() {
			this.$el.html(this.template(this.model.toJSON()));

			var toolsView = new app.ToolsView({
				model : this.model.get('tools')
			});
			toolsView.$el = this.$('.widget-content');
			toolsView.render();

			return this;
		}
	});
})(jQuery);