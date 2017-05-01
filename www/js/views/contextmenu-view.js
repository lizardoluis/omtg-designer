(function($) {
	'use strict';

	// Context Menu View
	// ----------

	app.ContextMenuView = Backbone.View.extend({

		className : 'context-menu',
		
		parentSelector: 'body',
		
		events : {
			'click' : 'destroy'
		},

		initialize : function(options) {
			console.log("initialize context menu"); 
			
			this.template = _.template($('#contextmenu-template').html());
			this.left = options.left;
			this.top = options.top;
			
			this.render();
		},

		render : function() {
			console.log("render context menu2"); 
			
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
		}
	});

})(jQuery);