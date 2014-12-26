var ConventionalView = Backbone.View.extend({
	model : new Diagram(),
//	tagName : 'div',
	events : {
		'click .edit' : 'edit',
		'click .delete' : 'delete',
		'blur .status' : 'close',
		'keypress .status' : 'onEnterUpdate'
	},
//	el: $('#diagrams-container'),
	initialize : function() {
		this.template = _.template($('#conventional-template').html());
	},
	// edit: function(ev) {
	// ev.preventDefault();
	// this.$('.status').attr('contenteditable', true).focus();
	// },
	// close: function(ev) {
	// var status = this.$('.status').text();
	// this.model.set('status', status);
	// this.$('.status').removeAttr('contenteditable');
	// },
	// onEnterUpdate: function(ev) {
	// var self = this;
	// if (ev.keyCode === 13) {
	// this.close();
	// _.delay(function() { self.$('.status').blur() }, 100);
	// }
	// },
	// delete: function(ev) {
	// ev.preventDefault();
	// tweets.remove(this.model);
	// },
	render : function() {
		this.$el.html(this.template(this.model.toJSON()));
		return this;
	}
});