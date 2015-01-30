(function($) {
	'use strict';

	// OMTG Connection Editor View
	// ----------

	app.omtg.ConnectionEditorView = Backbone.View.extend({

		id : 'connection-editor',

		className : 'modal fade',

		events : {
			// Modal events
			'click #btnUpdate' : 'updateDiagram',
			'hidden.bs.modal' : 'teardown',		
		},

		initialize : function(options) {
			this.template = _.template($('#omtg-connection-editor-template').html());
			
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

		updateDiagram : function() {
			
//			// Diagram type
//			var type = this.$('#inputDiagramType').data('type-name');
//			if (type) {
//				this.model.set('type', type);
//			}
//
//			// Diagram name
//			var name = this.$('#inputDiagramName').val().trim();
//			if (name) {
//				this.model.set('name', name);
//			}
//			
//			// Diagram attributes
//			this.model.set({'attributes': this.attrsClone});
//			this.model.trigger('change', this.model);

			this.teardown();
		},

	});

})(jQuery);