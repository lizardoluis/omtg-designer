(function($) {
	'use strict';

	// OMTG Diagram View
	// ----------
	
	app.omtg.DiagramView = Backbone.View.extend({

		tagName : 'div',

		className : 'diagram-container',
				
		events : {
			
			// Delete the diagram and remove its view from canvas
			'click .badge-delete' : 'deleteDiagram',
			
			// Toggle the selection of the diagram
			'click' : _.debounce(function(e) {
		            if (this.doucleclicked) {
		                this.doucleclicked = false;
		            } else {
		            	this.model.toggleSelected();
		            }
		        }, 200),
		      
		    // Open diagram editor popover
		    'dblclick' : function(e) {
		            this.doucleclicked = true;
		            this.edit.call(this, e);
		        },
		        
		    'mouseup' : 'updatePosition'
		},

		initialize : function() {

			// Templates
			this.$conventional = $('#omtg-conventional-template');
			this.$georeferenced = $('#omtg-georeferenced-template');

			// Listeners
			this.listenTo(this.model, 'change', this.render);
//			this.listenTo(this.model, 'destroy', this.remove);
		},

		render : function() {
			
			// Check the type of the diagram to use a proper template
			if (this.model.get("type") == 'conventional') {
				this.template = _.template(this.$conventional.html());
			} else {
				this.template = _.template(this.$georeferenced.html());
			}
			
			
			// Render class id
			this.el.id = this.model.get('id');
			
			// Set position
			this.$el.css({        
				'top': this.model.get('top') + 'px',
				'left': this.model.get('left') + 'px'
			});			
			
			// Render name and type
			this.$el.html(this.template(this.model.toJSON()));
			
			
			// Render attributes
			var attributes = this.model.get('attributes');						
			attributes.each(function(attribute) {				
				var attributeView = new app.omtg.AttributeView({model : attribute});
				this.$('.d-body > table > tbody').append(attributeView.render().el);
			}, this);
			
			
			// Render the `selected` state
			if(this.model.get('selected')){
				this.$el.addClass('selected');
				this.$('.badge-delete').removeClass('hidden');
			}
			else{
				this.$el.removeClass('selected');
			}
			
			if(app.canvas.get('diagramShadow')){
				this.$el.addClass('diagram-container-shadow');
			}
			
			app.plumbUtils.repaintAllAnchors();
			app.plumb.repaintEverything();
			
			return this;
		},
		
		edit : function() {
			
			var hasConnections = false;
			if(app.plumb.getConnections({ source: this.el.id }).length || app.plumb.getConnections({ target: this.el.id }).length)
				hasConnections = true;
		
			var modal = new app.omtg.DiagramEditorView({model : this.model, hasConnections : hasConnections});
		},
		
		deleteDiagram : function() {
						
			if (confirm(app.msgs.DELETE_DIAGRAM)){
			
				app.plumb.detachAllConnections(this.$el);
				
				// I could not use collection remove because it was not working, so I set a flag as deleted to avoid to be exported.
				this.model.set('deleted', true); 						
				
				// Remove view
				this.remove();
			}
		},
		
		updatePosition : function(event) {
			var grid = app.canvas.get("snapToGrid");
			this.model.set({
				'left': Math.round(this.$el.position().left / grid) * grid,
				'top' : Math.round(this.$el.position().top / grid) * grid
			});
						
			var plumbConnections = app.plumb.getConnections(this.$el);
			
			for (var i = 0; i < plumbConnections.length; i++) {
				app.plumbUtils.updateLabelsPosition(plumbConnections[i]);
			}
		}
	});

})(jQuery);