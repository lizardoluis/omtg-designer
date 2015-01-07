var app = app || {};

(function($) {
	'use strict';

	app.OMTGDiagramView = Backbone.View.extend({

		events : {

		},

		initialize : function() {
			this.template = _.template($('#omtg-diagram-template').html());

			this.listenTo(this.model, 'change', this.render);
		},

		render : function() {
			this.$el.html(this.template(this.model.toJSON()));

			// TODO:
			// var attributesView = new AttributesView({model:
			// this.model.get('tools')});
			// attributesView.$el = this.$('.diagram-attribute');
			// attributesView.render();

			return this;
		}

	});

})(jQuery);