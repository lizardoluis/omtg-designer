(function($) {
	'use strict';

	// OMTG Diagram Editor View
	// ----------

	app.omtg.DiagramEditorView = Backbone.View.extend({

		id : 'diagram-editor',

		className : 'modal fade',

		events : {

			// Modal events
			'click #btnUpdate' : 'updateDiagram',
			'hidden.bs.modal' : 'teardown',

			// Diagram events
			'click #ulDiagramType a' : 'selectType',
			'input #inputDiagramName':  'inputNameChanged',

			// Attributes table events
			'click #btnAddRow' : 'newAttribute',
			'click .btnRowDelete' : 'deleteAttribute',
			'click .btnRowUp' : 'moveAttributeUp',
			'click .btnRowDown' : 'moveAttributeDown',
			'blur .name-editable' : 'editName',
			'blur .value-editable' : 'editValue',
			'blur .length-editable' : 'editLength',
			'click .toggleKey': 'toggleKey',
			'click .toggleNotNull': 'toggleNotNull',
			'click .ulAttributeType a' : 'selectAttributeType',
			
			"submit" : "preventSubmission"
		},

		initialize : function(options) {
			this.template = _.template($('#omtg-diagram-editor-template').html());
			
			this.hasConnections = options.hasConnections;
			
			// Copy of attributes
			this.attrsClone = this.model.get('attributes').clone();

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
			
			// Disable diagram type selector if diagram has connections
			if(this.hasConnections){
				this.$("#inputDiagramType").addClass("disabled");
				this.$("#inputDiagramTypeToggle").addClass("disabled");
			}
			
			// Render table rows
			this.attrsClone.each(function(attr) {
				this.addAttribute(attr);
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
		
		inputNameChanged : function(event) {
			var name = this.$(event.currentTarget).val().trim();
			
			var reg = new RegExp("[a-zA-Z0-9][\w#@]{0,63}$");
			if (reg.test(name) && /\s/.test(name) == false) {
			    this.$('#formDiagramName').removeClass("has-error");
			    this.$('#btnUpdate').removeClass("disabled");
			} else {
				this.$('#formDiagramName').addClass("has-error");
				this.$('#btnUpdate').addClass("disabled");
			}
			
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
			
			// Diagram attributes, remove empty name ones
			for(var i=0; i<this.attrsClone.length; i++){
				var attr = this.attrsClone.at(i);
				if(attr.get('name') == ''){				
					this.attrsClone.remove(attr);
					i--;
				}
			}
			this.model.set({'attributes': this.attrsClone});
			this.model.trigger('change', this.model);

			this.teardown();
		},

		newAttribute : function() {
			var attr = new app.omtg.Attribute();
			this.addAttribute(attr);
			this.attrsClone.add(attr);
		},

		addAttribute : function(attribute) {
			var rowTemplate = _.template($('#omtg-attribute-row-editor-template').html());
			var html = rowTemplate(attribute.toJSON());
			this.$('#attrTable > tbody > tr:last').before(html);
		},

		deleteAttribute : function(event) {	
			var $row = this.$(event.currentTarget).closest('tr');
			this.attrsClone.remove(this.attrsClone.at($row.index()));
			$row.remove();
		},
		
		moveAttributeUp : function(event) {
			var $row = this.$(event.currentTarget).closest('tr');

			if ($row.index() > 0) {

				var att = this.attrsClone.at($row.index());
				this.attrsClone.remove(att);
				this.attrsClone.add(att, {at : $row.index() - 1});

				$row.insertBefore($row.prev());
			}
		},
		
		moveAttributeDown : function(event) {
			var $row = this.$(event.currentTarget).closest('tr');
			
			if($row.index() < this.attrsClone.length - 1){
				
				var att = this.attrsClone.at($row.index()+1);
				this.attrsClone.remove(att);
				this.attrsClone.add(att, {at : $row.index()});
				
				$row.insertAfter($row.next());
			}
		},
		
		editName : function(event) {
			var index = this.$(event.currentTarget).closest('tr').index();
			this.attrsClone.at(index).set('name', this.$(event.currentTarget).text());
		},
		
		editValue : function(event) {
			var index = this.$(event.currentTarget).closest('tr').index();
			this.attrsClone.at(index).set('defaultValue', this.$(event.currentTarget).text());
		},
		
		editLength : function(event) {
			var index = this.$(event.currentTarget).closest('tr').index();
			this.attrsClone.at(index).set('length', this.$(event.currentTarget).text());
		},
		
		toggleKey : function(event) {
			var index = this.$(event.currentTarget).closest('tr').index();
			this.attrsClone.at(index).set('isKey', this.$(event.currentTarget).prop('checked') );
		},
		
		toggleNotNull : function(event) {
			var index = this.$(event.currentTarget).closest('tr').index();
			this.attrsClone.at(index).set('isNotNull', this.$(event.currentTarget).prop('checked') );
		},
		
		selectAttributeType : function(event) {
			var index = this.$(event.currentTarget).closest('tr').index();
			
			var selected = this.$(event.currentTarget).text().trim();
			this.$(event.currentTarget).parent().parent().siblings('button.btnAttributeType:first').html(selected + ' <span class="caret"></span>');
			
			this.attrsClone.at(index).set('type', selected );
			
			if(selected == 'VARCHAR'){
				this.$(event.currentTarget).parent().parent().parent().parent().siblings('td.length-editable').attr('contenteditable','true');
			}
			else{
				this.$(event.currentTarget).parent().parent().parent().parent().siblings('td.length-editable').text('');
				this.$(event.currentTarget).parent().parent().parent().parent().siblings('td.length-editable').attr('contenteditable','false');
			}
		},
		
		// Avoid form submission on enter
		preventSubmission : function(event) {
			event.preventDefault();
		}
	});

})(jQuery);