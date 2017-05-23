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
					
				case Z_KEY:
					if(event.ctrlKey) {
						// CTRL + SHIFT + Z
						if(event.shiftKey) app.canvasView.redoHistory();
						// CTRL + Z
						else app.canvasView.undoHistory();  
					}
					break; 
			}
		}
		
	});

})(jQuery);