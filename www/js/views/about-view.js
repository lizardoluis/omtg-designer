(function($) {
	'use strict';

	// OMTG Connection Editor View
	// ----------

	app.AboutView = Backbone.View.extend({

		id : 'welcome-modal',

		className : 'modal fade',

		events : {
			// Modal events
			'hidden.bs.modal' : 'teardown',		
		},

		initialize : function(options) {
						
			this.template = _.template($('#about-template').html());
			
			this.render();
		},

		render : function() {
			
			this.$el.html(this.template());

			// Modal parameters
			this.$el.modal({
				backdrop : 'static',
				keyboard : true,
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