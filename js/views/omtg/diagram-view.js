(function($) {
	'use strict';

	// OMTG Diagram View
	// ----------
	
	app.omtg.DiagramView = Backbone.View.extend({

		tagName : 'div',

		className : 'diagram-container',
				
		events : {
			
			// Delete the diagram and remove its view from canvas
			'click .badge-delete' : 'delete',
			
			// Toggle the selection of the diagram
			'click' : _.debounce(function(e) {
		            if (this.doucleclicked) {
		                this.doucleclicked = false;
		            } else {
		                this.toggleSelected.call(this, e);
		            }
		        }, 200),
		      
		    // Open diagram editor popover
		    'dblclick' : function(e) {
		            this.doucleclicked = true;
		            this.edit.call(this, e);
		        }
		},

		initialize : function() {

			// Templates
			this.$conventional = $('#omtg-conventional-template');
			this.$georeferenced = $('#omtg-georeferenced-template');

			// Listeners
			this.listenTo(this.model, 'change', this.render);
			this.listenTo(this.model, 'destroy', this.remove);
			
			var attrs = new app.omtg.Attributes();
			attrs.add(new app.omtg.Attribute({isKey : true, name : 'id', type : 'Number'}));
			attrs.add(new app.omtg.Attribute({name : 'nome', type : 'Varchar', isNotNull : true, defaultValue : "Cruzeiro"}));
			attrs.add(new app.omtg.Attribute({name : 'cidade parapeito', type : 'Varchar'}));
			attrs.add(new app.omtg.Attribute({isKey : true, name : 'id', type : 'Number'}));
			attrs.add(new app.omtg.Attribute({name : 'nome', type : 'Varchar', isNotNull : true, defaultValue : "Cruzeiro"}));
			attrs.add(new app.omtg.Attribute({name : 'cidade parapeito', type : 'Varchar'}));
			
			this.model.set('attributes', attrs);
		},

		render : function() {
			
			// Check the type of the diagram to use a proper template
			if (this.model.get("type") == 'conventional') {
				this.template = _.template(this.$conventional.html());
			} else {
				this.template = _.template(this.$georeferenced.html());
			}
			
			
			// Render class id, name and type
			this.el.id = this.model.get('id');
			this.$el.html(this.template(this.model.toJSON()));
			
			
			// Render attributes
			var attributes = this.model.get('attributes');						
			attributes.each(function(attribute) {				
				var attributeView = new app.omtg.AttributeView({model : attribute});
				this.$('.diagram-attribute > table > tbody').append(attributeView.render().el);
			}, this);
			
			
			// Render the `selected` state
			if(this.model.get('selected')){
				this.$('.diagram').addClass('selected');
				this.$('.badge-delete').removeClass('hidden');
			}

			return this;
		},
		
		toggleSelected : function() {
			this.model.toggleSelected();
		},
		
		edit : function() {
			var modal = new app.omtg.DiagramEditorView({model : this.model});
		},
		
		delete : function() {
			this.model.trigger('destroy', this.model);
		},

	});

})(jQuery);