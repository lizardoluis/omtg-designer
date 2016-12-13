(function($) {
	'use strict';

	// Canvas View
	// ----------

	app.CanvasView = Backbone.View.extend({
		
		events : {
			'click' : 'clicked',
		},

		initialize : function() {
			this.listenTo(this.model.get('diagrams'), 'add', this.addOMTGDiagram);
			this.listenTo(this.model, 'change:activeTool', this.setCursor);
			this.listenTo(this.model, 'change:grid', this.toggleGrid);
			this.listenTo(this.model, 'change:diagramShadow', this.toggleDiagramShadow);
			
			$(document).on('keydown', this.keyAction);
		},

		clicked : function(event) {

			var tool = this.model.get('activeTool');

			if (tool) {				
				var grid = app.canvas.get("snapToGrid");				
				if (tool.get('model') == 'omtgDiagram') {
					var diagram = new app.omtg.Diagram({
						'type' : tool.get('name'),
						'left' : Math.round(event.offsetX / grid) * grid,
						'top' : Math.round(event.offsetY / grid) * grid
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
		
		toggleGrid : function() {			
			if(this.model.get('grid')){
				this.$el.addClass('canvas-background');
			}
			else{
				this.$el.removeClass('canvas-background');
			}
		},	
		
		toggleDiagramShadow : function() {	
			
			if(this.model.get('diagramShadow')){
				this.$el.find('.diagram-container').addClass('diagram-container-shadow');
			}
			else{
				this.$el.find('.diagram-container').removeClass('diagram-container-shadow');
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
			app.plumb.draggable(dObject, {
				containment : '#canvas',
				scroll : true,
				drag:function(e,ui) {
					// TODO: remove this drag function and repaint for performance reasons
					if($(".cartographic-square").length > 0)
						app.plumb.repaintEverything();
				}
			});
			
			app.plumb.makeSource(dObject, {
				filter : function() {
					var tool = app.canvas.get('activeTool');
					return tool != null && tool.get('model') == 'omtgRelation';
				}
			});
			
			app.plumb.makeTarget(dObject);	

			app.plumb.setSuspendDrawing(false, true);
		},
		
		print : function(){
			this.model.get('diagrams').unselectAll();
			
			this.$el.children().printThis({
				debug: false,
				importCSS: true, 
		        importStyle: false,        
				printContainer: true
			});
		},
		
		keyAction : function(event){
			
			if($(".modal-dialog").length == 0){
	
				var code = event.keyCode || event.which;
				
				var moveDiagram = function(direction, diff){
					event.preventDefault();
					app.canvas.get('diagrams').each(function(d) {
			    		if(d.get('selected'))		    		
			    			d.set(direction, d.get(direction) + diff);
			    	});
				};
							
				switch(code){
				case LEFT_ARROW_KEY:
					if(event.shiftKey) moveDiagram('left', -1);
					else moveDiagram('left', -4);
					break;
					
				case TOP_ARROW_KEY:
					if(event.shiftKey) moveDiagram('top', -1);
					else moveDiagram('top', -4);
					break;
					
				case RIGHT_ARROW_KEY:
					if(event.shiftKey) moveDiagram('left', 1);
					else moveDiagram('left', 4);
					break;
					
				case DOWN_ARROW_KEY:
					if(event.shiftKey) moveDiagram('top', 1);
					else moveDiagram('top', 4);
					break;
				}
			}
		}  
	});

})(jQuery);