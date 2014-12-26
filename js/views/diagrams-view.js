var diagrams = new Diagrams();

var DiagramsView = Backbone.View.extend({
	model : diagrams,
	el: $('#diagrams-container'),
	initialize : function() {
		this.model.on('add', this.render, this);
//		this.model.on('remove', this.render, this);
	},
	render : function() {
		var self = this;
		self.$el.html('');
		_.each(this.model.toArray(), function(diagram, i) {
			self.$el.append((new ConventionalView({	model : diagram	})).render().$el);
		});
		return this;
	}
});