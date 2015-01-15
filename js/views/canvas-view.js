(function($) {
	'use strict';

	// Canvas View
	// ----------

	app.CanvasView = Backbone.View.extend({

		events : {
			'click' : 'clicked',
//			'mouseover' : 'setCursor'
		},

		initialize : function() {
			this.listenTo(this.model.get('diagrams'), 'add', this.addOMTGDiagram);
			this.listenTo(this.model, 'change:activeTool', this.setCursor);
		},

		clicked : function(event) {

			var tool = this.model.get('activeTool');

			if (tool && tool.get('model') == 'omtgDiagram') {
				var diagram = new app.omtg.Diagram({
					'name' : 'Class Name',
					'type' : tool.get('name'),
					'left' : event.offsetX,
					'top' : event.offsetY,
				});
				this.model.get('diagrams').add(diagram);			
			}
			
			tool.toggleActive();
			this.model.set('activeTool', null);	
		},

		setCursor : function() {			
			
			var tool = this.model.get('activeTool');

			if (tool && tool.get('model') == 'omtgDiagram') {
				this.$el.css("cursor", "copy");
			}
			else{
				this.$el.css("cursor", "default");
			}			
		},		
		
		addOMTGDiagram : function(diagram) {
			var diagramView = new app.omtg.DiagramView({
				model : diagram
			});
			this.$el.append(diagramView.render().el);
		},
		
		
	});

})(jQuery);