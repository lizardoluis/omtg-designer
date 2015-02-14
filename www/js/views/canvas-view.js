(function($) {
	'use strict';

	// Canvas View
	// ----------

	app.CanvasView = Backbone.View.extend({

		events : {
			'click' : 'clicked',
		},

		initialize : function() {
			this.diagramCount = 1;
			this.listenTo(this.model.get('diagrams'), 'add', this.addOMTGDiagram);
			this.listenTo(this.model, 'change:activeTool', this.setCursor);
		},

		clicked : function(event) {

			var tool = this.model.get('activeTool');

			if (tool) {
				if (tool.get('model') == 'omtgDiagram') {
					var diagram = new app.omtg.Diagram({
						'name' : 'Class ' + this.diagramCount++,
						'type' : tool.get('name'),
						'left' : event.offsetX,
						'top' : event.offsetY,
					});
					this.model.get('diagrams').add(diagram);
				}
				tool.toggleActive();
				this.model.set('activeTool', null);
			}
			
			this.model.get('diagrams').unselectAll();
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
			
			app.plumb.setSuspendDrawing(true);			
			
			var diagramView = new app.omtg.DiagramView({
				model : diagram
			});
			
			var dObject = diagramView.render().el;
			this.$el.append(dObject);
			
			
			//Plumbing	
			app.plumb.makeSource(dObject, {
				filter : function() {
					var tool = app.canvas.get('activeTool');
					if(tool && tool.get('model') == 'omtgRelation')
						return true;
					return false;
				},
			});
			
			app.plumb.makeTarget(dObject);	
			
			app.plumb.draggable(dObject, {
				containment : '#canvas',
				scroll : true,
				drag:function(e,ui) {
					// TODO: remove this drag function and repaint for performance reasons
					if($(".cartographic-square").length > 0 || $(".generalization-triangle").length > 0)
						app.plumb.repaintEverything();
				}
			});

			app.plumb.setSuspendDrawing(false, true);
		},
		
		
	});

})(jQuery);