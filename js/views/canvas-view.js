(function ($) {
	'use strict';
	
	// Canvas View
	// ----------

	app.CanvasView = Backbone.View.extend({

		initialize: function () {
						
			this.listenTo(this.model, 'add', this.addOMTGDiagram);
		},
		
		addOMTGDiagram: function(diagram) {			
			var diagramView = new app.omtg.DiagramView({model: diagram});
			this.$el.append(diagramView.render().el);
		}		
	});
		
})(jQuery);