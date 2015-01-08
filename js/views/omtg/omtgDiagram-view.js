var app = app || {};

(function($) {
	'use strict';

	// OMTGDiagram View
	// ----------
	
	app.OMTGDiagramView = Backbone.View.extend({

		events : {
		// TODO:
		},

		initialize : function() {

			this.$conventional = $('#omtg-conventional-template');
			this.$georeferenced = $('#omtg-georeferenced-template');

			this.listenTo(this.model, 'change', this.render);
		},

		render : function() {
			
			if (this.model.get("type") == 'conventional') {
				this.template = _.template(this.$conventional.html());
			} else {
				this.template = _.template(this.$georeferenced.html());
			}

			this.$el.html(this.template(this.model.toJSON()));
			
						
			// Render attributes
			var attributes = this.model.get('attributes');					
			
			attributes.each(function(attribute) {
				
				var attributeView = new app.OMTGAttributeView({model : attribute});
				this.$('.diagram-attribute > table > tbody').append(attributeView.render().el);
			}, this);

			return this;
		}

	});

})(jQuery);