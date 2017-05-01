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
			'click' : 'destroy',
			'contextmenu' : 'rightClick'
		},

		initialize : function(options) {			
			this.template = _.template($('#contextmenu-template').html());
			this.left = options.left;
			this.top = options.top;
			this.diagramView = options.diagramView;
			
			this.render();
		},

		render : function() {			
			this.$el.html(this.template());   			
			this.$el.appendTo(this.parentSelector);
			
			//TODO: chose position to better fit the canvas
			this.$('.context-menu-content').css({ 
				top: this.top + 'px', 
				left: this.left + 'px' 
			});	
			
			return this;
		},
		
		destroy: function() {			
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
		
		rightClick : function(event) {
			event.preventDefault();
			this.destroy();
		}
	});

})(jQuery);