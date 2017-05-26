(function($) {
	'use strict';

	// Body View
	// ----------

	app.BodyView = Backbone.View.extend({

		el: "body",
		
		events : {
			"keydown": "keyHandler",
		},

		keyHandler : function(event){
						
			// Avoid breaking the dialogs
			if($(".modal-dialog").length > 0)
				return;
	
			var code = event.keyCode || event.which;
						
			switch(code){		
				case LEFT_ARROW_KEY:
					if(event.shiftKey) _.invoke(app.canvas.get('diagrams').getSelected(), 'move', 0, -1);					
					else _.invoke(app.canvas.get('diagrams').getSelected(), 'move', 0, -4);
					break;
					
				case TOP_ARROW_KEY:
					if(event.shiftKey) _.invoke(app.canvas.get('diagrams').getSelected(), 'move', -1, 0);
					else _.invoke(app.canvas.get('diagrams').getSelected(), 'move', -4, 0);
					break;
					
				case RIGHT_ARROW_KEY:
					if(event.shiftKey) _.invoke(app.canvas.get('diagrams').getSelected(), 'move', 0, 1);
					else _.invoke(app.canvas.get('diagrams').getSelected(), 'move', 0, 4);
					break;
					
				case DOWN_ARROW_KEY:
					if(event.shiftKey) _.invoke(app.canvas.get('diagrams').getSelected(), 'move', 1, 0);
					else _.invoke(app.canvas.get('diagrams').getSelected(), 'move', 4, 0);
					break;
					
				case DELETE_KEY:
					var selected = app.canvas.get('diagrams').getSelected();
					app.canvas.get('diagrams').removeSet(selected);
					break; 
					
				case C_KEY:
					if(event.ctrlKey){
						event.preventDefault();
						var selected = app.canvas.get('diagrams').getSelected();
						if(selected.length == 1){
							app.canvas.copyDiagram(selected[0]); 
						}
					}	
					break;
					
				case D_KEY:
					if(event.ctrlKey){
						event.preventDefault();
						var selected = app.canvas.get('diagrams').getSelected();
						if(selected.length == 1){
							app.canvas.duplicateDiagram(selected[0]); 
						}
					}		
					break;
				
				case B_KEY:
					if(event.ctrlKey && event.shiftKey){
						var selected = app.canvas.get('diagrams').getSelected();
						if(selected.length == 1){
							selected[0].trigger('sendtoback', selected[0]); 
						}
					}		
					break;
					
				case F_KEY:
					if(event.ctrlKey && event.shiftKey){
						var selected = app.canvas.get('diagrams').getSelected();
						if(selected.length == 1){
							selected[0].trigger('bringtofront', selected[0]); 
						}
					}					
					break;
					
				case V_KEY:
					if(event.ctrlKey){
						var offset = Math.floor(Math.random() * 41);
						app.canvas.pasteDiagram(10+offset, 10+offset);
					}
					break;
				
				case Z_KEY:
					if(event.ctrlKey) {
						// CTRL + SHIFT + Z
						if(event.shiftKey) app.canvasView.redoHistory();
						// CTRL + Z
						else app.canvasView.undoHistory();  
					}
					break;
					
				case F1_KEY:
					event.preventDefault();
					new app.AboutView();
					break;
					
				case F2_KEY:
					var selected = app.canvas.get('diagrams').getSelected();
					if(selected.length == 1){
						selected[0].trigger('edit', selected[0]); 
					}
					break;
			}
		}
		
	});

})(jQuery);