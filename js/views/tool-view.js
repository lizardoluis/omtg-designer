var ToolView = Backbone.View.extend({

	initialize : function() {	
		this.template = _.template($('#tool-template').html());				
	},

	render : function() {
		var html = this.template(this.model.toJSON());
		this.setElement(html);
		return this;
	}
});
