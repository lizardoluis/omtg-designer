(function($) {
	'use strict';

	// OMTGDiagram View
	// ----------

	app.omtg.AttributeView = Backbone.View.extend({

		tagName : 'tr',

		initialize : function() {
			this.template = _.template("<td><%= attr %></td>");
		},

		render : function() {
			// TODO: include other attr fields
			var attrText = this.model.escape('name');
			this.$el.html(this.template({attr : attrText}));
			return this;
		},

	});

})(jQuery);