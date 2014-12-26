//var tools = relationsTools;

var ToolsView = Backbone.View.extend({

	render: function() {
		
	    this.$el.empty();
	
	    this.model.each(function( tool ){	    	
	    	var toolView = new ToolView({model : tool});
	    	this.$el.append(toolView.render().el);
	    }, this);
	    
	    return this;
	}
});