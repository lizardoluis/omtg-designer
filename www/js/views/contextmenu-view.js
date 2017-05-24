(function($) {
	'use strict';

	// Context Menu View
	// ----------

	app.ContextMenuView = Backbone.View.extend({

		className : 'context-menu',
		
		parentSelector: 'body',
		
		events : {
			'click  #cmEdit' : 'editDiagram',
			'click  #cmDelete' : 'deleteDiagram',
			'click  #cmToFront' : 'bringToFront',
			'click  #cmToBack' : 'sendToBack',
			'click  #cmCopy' : 'copyDiagram',
			'click  #cmPaste' : 'pasteDiagram',
			'click  #cmDuplicate' : 'duplicateDiagram',
			'click  #cmUndo' : 'undo',
			'click  #cmRedo' : 'redo',
			'click' : 'destroyMenu',
			'contextmenu' : 'rightClick'
		},

		initialize : function(options) {		
			
			if(options.diagramView != null){
				this.template = _.template($('#contextmenu-diagram-template').html());
				this.diagramView = options.diagramView;
			}
			else{
				this.template = _.template($('#contextmenu-canvas-template').html());
				this.offsetTop = options.offsetTop;
				this.offsetLeft = options.offsetLeft; 
			}
						
			this.left = options.left;
			this.top = options.top;			
			
			this.render();
		},

		render : function() {			
			this.$el.html(this.template());   			
			this.$el.appendTo(this.parentSelector);
					
			// Context menu of canvas 
			if(this.diagramView == null){
				
				// set paste inactive if there is nothing on clipboard
				if(app.canvas.get('clipboard') == null){
					this.$('#cmPaste').parent().addClass('disabled');  
				} 
				
				// set undo and redo inactive
				var undoManager = app.canvas.get('undoManager');				
				if(!undoManager.hasUndo())
					this.$('#cmUndo').parent().addClass('disabled');				
				if(!undoManager.hasRedo())
					this.$('#cmRedo').parent().addClass('disabled');  
			} 
			
			//TODO: chose position to better fit the canvas
			this.$('.context-menu-content').css({ 
				top: this.top + 'px', 
				left: this.left + 'px' 
			});	
			
			return this;
		},
		
		destroyMenu: function() {				
		    // COMPLETELY UNBIND THE VIEW
		    this.undelegateEvents();

		    this.$el.removeData().unbind(); 

		    // Remove view from DOM
		    this.remove();  
		    Backbone.View.prototype.remove.call(this);
		},
		
		editDiagram : function() { 
			this.diagramView.edit(); 
		},
		
		deleteDiagram : function() {
			this.diagramView.deleteDiagram();
		},
		
		bringToFront : function() {
			this.diagramView.bringToFront();
		},
		
		sendToBack : function() {
			this.diagramView.sendToBack(); 
		},
		
		duplicateDiagram : function() {
			this.diagramView.duplicate();
			app.canvasView.updateHistory();
		}, 
		
		copyDiagram : function() {
			this.diagramView.copy();
		}, 
		
		pasteDiagram : function(event) {	
						
			var clipboard = app.canvas.get('clipboard');
			
			if(clipboard != null){ 
								
				var diagram = new app.omtg.Diagram({
					'type' : clipboard.get('type'),
					'attributes' : clipboard.get('attributes'),
					'left' : this.offsetLeft,
					'top' : this.offsetTop,	 
				});
				
				//TODO: repetitive code from diagram_view
				for(var i=1; ; i++){
					var copyName = clipboard.get('name') + '_' + i;
					if( app.canvas.get('diagrams').findByName(copyName) == null ){
						diagram.set('name', copyName );
						break;
					}
				}		
				 
				app.canvas.get('diagrams').add(diagram);
				app.canvasView.updateHistory();
			}
			else {
				event.stopPropagation(); 
			}
		}, 
		
		rightClick : function(event) {
			event.preventDefault();
			this.destroyMenu(); 
		},
		
		undo : function(event) {					
			if(app.canvas.get('undoManager').hasUndo()){
				app.canvasView.undoHistory();
			}
			else{
				event.stopPropagation(); 
			}			
		}, 
		
		redo : function(event) {
			if(app.canvas.get('undoManager').hasRedo()){
				app.canvasView.redoHistory();
			}
			else{
				event.stopPropagation(); 
			}
		}		
	});

})(jQuery);