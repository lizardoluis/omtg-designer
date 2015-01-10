var app = app || {};

(function($) {
	'use strict';

	// OMTGDiagram View
	// ----------

	app.OMTGDiagramEditorView = Backbone.View.extend({

		id : 'diagram-editor',

		className : 'modal fade',

		events : {
			'hidden.bs.modal' : 'teardown',
			"click #ulDiagramType a" : "selectType",
			"click #btnUpdate" : "updateDiagram",
		},

		initialize : function(options) {
			this.template = _.template($('#omtg-diagram-editor-template')
					.html());

			this.render();
		},

		render : function() {
			this.$el.html(this.template(this.model.toJSON()));

			// Modal parameters
			this.$el.modal({
				backdrop : 'static',
				keyboard : false,
				show : true,
			});

			return this;
		},

		// Remove and delete from DOM the modal
		teardown : function() {
			this.$el.data('modal', null);
			this.remove();
		},

		// Selected the option in the diagram type dropdown
		selectType : function(event) {
			var selected = this.$(event.currentTarget).html();
			this.$('#inputDiagramType').html(selected);
			
			var type = this.$(event.currentTarget).data('type-name');
			this.$('#inputDiagramType').data('type-name', type);
		},
		
		updateDiagram : function() {
			
			// Diagram type
			var type = this.$('#inputDiagramType').data('type-name');
			if(type){
				this.model.set('type', type);
			}
			
			// Diagram name
			var name = this.$('#inputDiagramName').val().trim();
			if(name){
				this.model.set('name', name);
			}
						
			this.teardown();
		},
	});

})(jQuery);

// $("#ulDropdown li a").click(function(){
// var selText = $(this).html();
// $("#btnDropdown").html(selText);
// });
