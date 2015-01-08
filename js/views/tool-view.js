var app = app || {};

(function($) {
	'use strict';
	
	// Tool View
	// ----------

	app.ToolView = Backbone.View.extend({

		events : {
			'click' : 'clicked',
		},
		
		initialize : function() {
			this.template = _.template($('#tool-template').html());
		},

		render : function() {
			var html = this.template(this.model.toJSON());
			this.setElement(html);
			return this;
		},

		clicked : function() {
			
			if (this.model.get('model') == 'omtgDiagram'){
				
				var newDiagram = new app.OMTGDiagram();
				newDiagram.set('name', 'Class Name');
				newDiagram.set('type', this.model.get('name'));
								
				app.diagrams.add(newDiagram);				
			}
		}
	});
	
})(jQuery);