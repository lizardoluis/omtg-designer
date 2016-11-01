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
			
			'click #ulConnectionSpatialRelation a' : 'selectSpatialRelation',
			
			"submit" : "preventSubmission",
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
			
			// Add Spatial Relation component
			if(type == 'spatial-association'){
				
				fieldset.append(_.template($('#omtg-connection-editor-spatial-relation-template').html()));				
				this.descriptionLabel = this.connection.getOverlay("description-label");			
				
				var spatialRelation = this.descriptionLabel.getLabel().split(" ")[0];
				
				if(spatialRelation){
					this.$('#inputConnectionSpatialRelation').data('spatialrelation', spatialRelation);
					this.$('#inputConnectionSpatialRelation').html(spatialRelation);
				}
				else{
					this.$('#inputConnectionSpatialRelation').data('spatialrelation', 'Contains');
					this.$('#inputConnectionSpatialRelation').html('Contains');
				}
				
				if(spatialRelation.toLowerCase() == 'near' || spatialRelation.toLowerCase() == 'distant'){
					this.$('#inputConnectionDistance').prop("disabled", false);
					this.$('#inputConnectionDistance').val(this.connection.getParameter("distance"));
				}
			}
			
			// Add Description component
			if(type == 'association' || type == 'arc-network' || type == 'arc-network-self'){
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
			var spatialRelation;
				
			// Spatial connection description
			if(type == 'spatial-association'){
				spatialRelation = this.$('#inputConnectionSpatialRelation').data('spatialrelation');
				
				if(spatialRelation.toLowerCase() == 'near' || spatialRelation.toLowerCase() == 'distant'){
					var dist = this.$('#inputConnectionDistance').val().trim();
					
					// Check if distance is number and available, if not set 10 as default.
					if(!dist || !(dist>=0))
						dist = 10;
					
					this.connection.setParameter("distance", dist);
					this.descriptionLabel.setLabel(spatialRelation + ' (' + dist + ')');
				}	
				else{
					this.descriptionLabel.setLabel(spatialRelation);
				}
			}
			
			// Connection description
			if(type == 'association' || type == 'arc-network' || type == 'arc-network-self'){
				var description = this.$('#inputConnectionDescription').val().trim();
				this.descriptionLabel.setLabel(description);
			}
			
			if(type == 'association' || type == 'spatial-association'){
				// Connection cardinality A
				var minA = this.$('#inputMinA').val().trim();
				var maxA = this.$('#inputMaxA').val().trim();
				minA = minA >= 0 && minA != ""  ? minA : '0';
				maxA = maxA >= 1 && maxA != "" && minA <= maxA ? maxA : '*';
				this.cardLabelA.setLabel(this.concatCardLabel(minA, maxA));

				// Connection cardinality B
				var minB = this.$('#inputMinB').val().trim();
				var maxB = this.$('#inputMaxB').val().trim();
				minB = minB >= 0 && minB != ""  ? minB : '0';
				maxB = maxB >= 1 && maxB != "" && minB <= maxB ? maxB : '*';
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
			
			
			//this.teardown();
		},
		
		detach : function(event) {
			
			if (confirm(app.msgs.deleteConnection)){
				
				var type = this.connection.getType()[0];

				// Detach leg connections of the cartographic
				if(type == 'cartographic-generalization-disjoint' || type == 'cartographic-generalization-overlapping'){
					app.plumb.detachAllConnections(this.connection.getOverlay("cartographic-square").getElement());		
				}
				else if(type == 'arc-network' || type == 'arc-network-sibling' || type == 'arc-network-self' || type == 'arc-network-sibling-self'){
					var sibling = this.connection.getParameter("sibling");
					app.plumb.detach(sibling);
				}
				
				app.plumb.detach(this.connection);
				
				//this.teardown();
			}
		},
		
		concatCardLabel : function(min, max){

			if(min == max)
				return min;

			return min + ".." + max;
		},
		
		// Selected the option in the diagram type dropdown
		selectSpatialRelation : function(event) {
			var selected = this.$(event.currentTarget).html();
			this.$('#inputConnectionSpatialRelation').html(selected);

			var spatialrelation = this.$(event.currentTarget).data('spatialrelation').trim();
			this.$('#inputConnectionSpatialRelation').data('spatialrelation', spatialrelation);
			
			if(spatialrelation.toLowerCase() == 'near' || spatialrelation.toLowerCase() == 'distant'){
				this.$('#inputConnectionDistance').prop("disabled", false);
				this.$('#inputConnectionDistance').val(this.connection.getParameter("distance"));
			}
			else{
				this.$('#inputConnectionDistance').prop("disabled", true);
				this.$('#inputConnectionDistance').val("");
				this.connection.setParameter("distance", "");
			}
		},
		
		// Avoid form submission on enter
 		preventSubmission : function(event) {
 			event.preventDefault();
  		}

	});

})(jQuery);