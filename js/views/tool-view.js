var app = app || {};

(function($) {
	'use strict';

	app.ToolView = Backbone.View.extend({

		initialize : function() {
			this.template = _.template($('#tool-template').html());
		},

		events : {
			'click' : 'clicked',
		},

		render : function() {
			var html = this.template(this.model.toJSON());
			this.setElement(html);
			return this;
		},

		clicked : function() {
			
			var type = this.model.get('type');
			if (type == 'georeferenced' || type == 'conventional'){
				
				var newDiagram = new app.Diagram();
				newDiagram.set('name', 'Class Name');
				newDiagram.set('type', this.model.get('name'));
				
				app.diagrams.add(newDiagram);				
			}
		}
	});
	
})(jQuery);