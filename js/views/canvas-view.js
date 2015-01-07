var app = app || {};

(function ($) {
	'use strict';

	app.CanvasView = Backbone.View.extend({

		el: '#canvas',

		initialize: function () {
						
			this.listenTo(app.diagrams, 'add', this.addDiagram);
		},
		
		addDiagram: function(diagram) {			
			var diagramView = new app.OMTGDiagramView({model: diagram});
			this.$el.append(diagramView.render().el);
		}		
	});
		
})(jQuery);