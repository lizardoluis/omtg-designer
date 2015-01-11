var app = app || {};

(function($) {
	'use strict';

	// OMTGDiagram View
	// ----------

	app.OMTGDiagramEditorView = Backbone.View.extend({

		id : 'diagram-editor',

		className : 'modal fade',

		events : {

			// Modal events
			'click #btnUpdate' : 'updateDiagram',
			'hidden.bs.modal' : 'teardown',

			// Diagram events
			'click #ulDiagramType a' : 'selectType',

			// Attributes table events
			'click #btnAddRow' : 'newAttribute',
			'click .btnRowDelete' : 'deleteAttribute',
			'click .btnRowUp' : 'moveAttributeUp',
			'click .btnRowDown' : 'moveAttributeDown',
			'blur .name-editable' : 'editName',
			'blur .value-editable' : 'editValue',
			'click .toggleKey': 'toggleKey',
			'click .toggleNotNull': 'toggleNotNull',
			'click .ulAttributeType a' : 'selectAttributeType',
			
		},

		initialize : function(options) {
			this.template = _.template($('#omtg-diagram-editor-template').html());
			
			// Copy of attributes
			this.attributes = this.model.get('attributes');

			this.render();
		},

		render : function() {
			this.$el.html(this.template(this.model.toJSON()));

			// Modal parameters
			this.$el.modal({
				backdrop : 'static',
				keyboard : false,
				show : true,
			});

			// Render table rows
			this.attributes.each(function(attribute) {
				this.addAttribute(attribute);
			}, this);

			return this;
		},

		// Remove and delete from DOM the modal
		teardown : function() {
			this.$el.data('modal', null);
			this.remove();
		},

		// Selected the option in the diagram type dropdown
		selectType : function(event) {
			var selected = this.$(event.currentTarget).html();
			this.$('#inputDiagramType').html(selected);

			var type = this.$(event.currentTarget).data('type-name');
			this.$('#inputDiagramType').data('type-name', type);
		},

		updateDiagram : function() {

			// Diagram type
			var type = this.$('#inputDiagramType').data('type-name');
			if (type) {
				this.model.set('type', type);
			}

			// Diagram name
			var name = this.$('#inputDiagramName').val().trim();
			if (name) {
				this.model.set('name', name);
			}
			
			// Diagram attributes
			this.model.set({'attributes': this.attributes});
			this.model.trigger('change', this.model);

			this.teardown();
		},

		newAttribute : function() {
			var attr = new app.OMTGAttribute();
			this.addAttribute(attr);
			this.attributes.add(attr);
		},

		addAttribute : function(attribute) {
			var rowTemplate = _.template($('#omtg-attribute-row-editor-template').html());
			var html = rowTemplate(attribute.toJSON());
			this.$('#attrTable > tbody > tr:last').before(html);
		},

		deleteAttribute : function(event) {	
			var $row = this.$(event.currentTarget).closest('tr');
			this.attributes.remove(this.attributes.at($row.index()));
			$row.remove();
		},
		
		moveAttributeUp : function(event) {
			var $row = this.$(event.currentTarget).closest('tr');

			if ($row.index() > 0) {

				var att = this.attributes.at($row.index());
				this.attributes.remove(att);
				this.attributes.add(att, {at : $row.index() - 1});

				$row.insertBefore($row.prev());
			}
		},
		
		moveAttributeDown : function(event) {
			var $row = this.$(event.currentTarget).closest('tr');
			
			if($row.index() < this.attributes.length - 1){
				
				var att = this.attributes.at($row.index()+1);
				this.attributes.remove(att);
				this.attributes.add(att, {at : $row.index()});
				
				$row.insertAfter($row.next());
			}
		},
		
		editName : function(event, ha) {
			var index = this.$(event.currentTarget).closest('tr').index();
			this.attributes.at(index).set('name', this.$(event.currentTarget).text());
		},
		
		editValue : function(event) {
			var index = this.$(event.currentTarget).closest('tr').index();
			this.attributes.at(index).set('defaultValue', this.$(event.currentTarget).text());
		},
		
		toggleKey : function(event) {
			var index = this.$(event.currentTarget).closest('tr').index();
			this.attributes.at(index).set('isKey', this.$(event.currentTarget).prop('checked') );
		},
		
		toggleNotNull : function(event) {
			var index = this.$(event.currentTarget).closest('tr').index();
			this.attributes.at(index).set('isNotNull', this.$(event.currentTarget).prop('checked') );
		},
		
		selectAttributeType : function(event) {
			var index = this.$(event.currentTarget).closest('tr').index();
			
			var selected = this.$(event.currentTarget).text();
			this.$(event.currentTarget).parent().parent().siblings('button.btnAttributeType:first').html(selected + ' <span class="caret"></span>');
			
			this.attributes.at(index).set('type', selected );
		},
	});

})(jQuery);