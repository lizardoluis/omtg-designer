var app = app || {};

(function ($) {
	'use strict';
	
	// Canvas View
	// ----------

	app.CanvasView = Backbone.View.extend({

		el: '#canvas',

		initialize: function () {
						
			this.listenTo(app.diagrams, 'add', this.addOMTGDiagram);
		},
		
		addOMTGDiagram: function(diagram) {			
			var diagramView = new app.OMTGDiagramView({model: diagram});
			this.$el.append(diagramView.render().el);
		}		
	});
		
})(jQuery);