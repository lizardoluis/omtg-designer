(function($) {
	'use strict';

	// XML Importer View
	// ----------

	app.XMLImporterView = Backbone.View.extend({

		id : 'xml-importer',

		className : 'modal fade',

		events : {

			// Modal events
			'click #btnImport' : 'import',
			'hidden.bs.modal' : 'teardown',			
		},

		initialize : function(options) {
			this.template = _.template($('#xmlimporter-template').html());
			this.render();
		},

		render : function() {
			this.$el.html(this.template());

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

	});

})(jQuery);