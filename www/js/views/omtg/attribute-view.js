(function($) {
	'use strict';

	// OMTGDiagram View
	// ----------

	app.omtg.AttributeView = Backbone.View.extend({

		tagName : 'tr',

		initialize : function() {
			this.template = _.template("<td><% if( isKey ){ %> <img class='attribute-key' src='imgs/omtg/key.png'> <% } %></td><td><%= attr %></td>");
			
		},

		render : function() {
			// TODO: include other attr fields
			this.$el.html(this.template({
				isKey : this.model.get('isKey'), 
				attr : this.model.get('type') == 'VARCHAR' ? this.model.escape('name') + ': ' + this.model.get('type') + '('+ this.model.get('length') +')' : this.model.escape('name') + ': ' + this.model.get('type'),
			}));
			return this;
		},

	});

})(jQuery);