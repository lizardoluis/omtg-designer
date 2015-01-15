(function($) {
	'use strict';

	// Tool View
	// ----------

	app.ToolView = Backbone.View.extend({

		events : {
			'click' : 'clicked',
		},

		initialize : function() {
			this.template = _.template($('#tool-template').html());

			// Listener
			this.listenTo(this.model, 'change:active', this.toggle);
		},

		render : function() {
			var html = this.template(this.model.toJSON());
			this.setElement(html);

			return this;
		},

		clicked : function() {
			this.model.toggleActive();
		},

		toggle : function() {
			
			if (this.model.get('active')) {
				this.$el.addClass('active');
				app.canvas.set('activeTool', this.model);				
				
			} else {
				this.$el.removeClass('active');
				app.canvas.set('activeTool', null);
			}
		},

	});

})(jQuery);