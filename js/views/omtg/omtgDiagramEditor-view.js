var app = app || {};

(function($) {
	'use strict';

	// OMTGDiagram View
	// ----------
	
	app.OMTGDiagramEditorView = Backbone.View.extend({
				
		events : {

		},

		initialize : function(options) {
			this.template = _.template($('#omtg-diagram-editor-template').html());
			
			if (options && options.diagram) {
				this.diagram = options.diagram;
			}
			
			this.render();
		},

		render : function() {
			var html = this.template(this.model.toJSON());
		
			this.diagram.popover({
	            html: true,
	            title: 'OMT-G Diagram Editor',
	            content: html,
	            viewport: $("#canvas"),
	            placement: 'auto',
	            trigger: 'manual'
	        });
						
			this.diagram.popover('show');
						
			return this;
		},
		
		destroy: function () {
			this.diagram.popover("destroy");
			this.diagram.remove();
			this.diagram = null; //TODO: check this
		},

	});

})(jQuery);