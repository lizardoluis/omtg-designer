(function($) {
	'use strict';

	// OMTG Connection Editor View
	// ----------

	app.omtg.ConnectionEditorView = Backbone.View.extend({

		id : 'connection-editor',

		className : 'modal fade',

		events : {
			// Modal events
			'click #btnUpdate' : 'update',
			'click #btnDelete' : 'detach',
			'hidden.bs.modal' : 'teardown',		
		},

		initialize : function(options) {
						
			this.template = _.template($('#omtg-connection-editor-template').html());
			
			this.connection = options.connection;
			
			this.render();
		},

		render : function() {
			
			this.$el.html(this.template());
			var fieldset = this.$('#connection-editor-form > fieldset');
						
			var type = this.connection.getType()[0];
			
			// Add Description component
			if(type == 'association' || type == 'spatial-association' || type == 'arc-network' || type == 'arc-network-self'){
				fieldset.append(_.template($('#omtg-connection-editor-description-template').html()));
				
				this.descriptionLabel = this.connection.getOverlay("description-label");			
				this.$('#inputConnectionDescription').val(this.descriptionLabel.getLabel());
			}
			
			// Add Cardinalities component
			if(type == 'association' || type == 'spatial-association'){
				fieldset.append(_.template($('#omtg-connection-editor-cardinalities-template').html()));
				
				this.cardLabelA = this.connection.getOverlay("cardinality-labelA");			
				this.$('#inputMinA').val(this.connection.getParameter("minA"));
				this.$('#inputMaxA').val(this.connection.getParameter("maxA"));
				
				this.cardLabelB = this.connection.getOverlay("cardinality-labelB");			
				this.$('#inputMinB').val(this.connection.getParameter("minB"));
				this.$('#inputMaxB').val(this.connection.getParameter("maxB"));
			}
			
			// Add Cartographic component
			if(type == 'cartographic-generalization-disjoint' || type == 'cartographic-generalization-overlapping'){
				fieldset.append(_.template($('#omtg-connection-editor-cartographic-template').html()));
				
				this.cartographicLabel = this.connection.getOverlay("cartographic-label");
				if(this.cartographicLabel.getLabel() == 'scale') 
					this.$('#scaleRadio').prop("checked", true);
				else
					this.$('#shapeRadio').prop("checked", true);
			}

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

		update : function() {
			
			var type = this.connection.getType()[0];
			
			// Connection description
			if(type == 'association' || type == 'spatial-association' || type == 'arc-network' || type == 'arc-network-self'){
				var description = this.$('#inputConnectionDescription').val().trim();
				this.descriptionLabel.setLabel(description);
			}
			
			if(type == 'association' || type == 'spatial-association'){
				// Connection cardinality A
				var minA = this.$('#inputMinA').val().trim();
				var maxA = this.$('#inputMaxA').val().trim();			
				this.cardLabelA.setLabel(this.concatCardLabel(minA, maxA));

				// Connection cardinality B
				var minB = this.$('#inputMinB').val().trim();
				var maxB = this.$('#inputMaxB').val().trim();
				this.cardLabelB.setLabel(this.concatCardLabel(minB, maxB));			
			
				// Save in the connection parameters
				this.connection.setParameter("minA", minA);
				this.connection.setParameter("maxA", maxA);
				this.connection.setParameter("minB", minB);
				this.connection.setParameter("maxB", maxB);
			}
			
			if(type == 'cartographic-generalization-disjoint' || type == 'cartographic-generalization-overlapping'){
				this.cartographicLabel.setLabel(this.$('input:radio[name=inlineRadioOptions]:checked').val());
			}
			
			
			this.teardown();
		},
		
		detach : function(event) {
			
			if (confirm(app.msgs.deleteConnection)){
				
				var type = this.connection.getType()[0];

				// Detach leg connections of the cartographic
				if(type == 'cartographic-generalization-disjoint' || type == 'cartographic-generalization-overlapping'){
					app.plumb.detachAllConnections(this.connection.getOverlay("cartographic-square").getElement());		
				}

				app.plumb.detach(this.connection);
				this.teardown();
			}
		},
		
		concatCardLabel : function(min, max){
			if(min == "" && max == ""){
				return "";
			}

			if (min != "" && max != ""){
				return min + ".." + max;
			}

			return min + "" + max;
		},

	});

})(jQuery);