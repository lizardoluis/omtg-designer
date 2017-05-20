(function($) {
	'use strict';

	// XML Importer View
	// ----------

	app.XMLImporterView = Backbone.View.extend({

		id : 'xml-importer',

		className : 'modal fade',
		
		events : {

			// Modal events
			'click #btnImport' : 'importXML',
			
			'hidden.bs.modal' : 'teardown',
			
			'change #js-upload-files' : 'uploadFile',
			
			'load #js-upload-files' : 'importXML'
		},

		initialize : function(options) {
			this.template = _.template($('#xmlimporter-template').html());
			this.render();
		},

		render : function() {
			this.$el.html(this.template());

			// Modal parameters
			this.$el.modal({
				backdrop : 'static',
				keyboard : false,
				show : true
			});

			return this;
		},

		// Remove and delete from DOM the modal
		teardown : function() {
			this.$el.data('modal', null);
			this.remove();
		},
		
		uploadFile : function(evt) {
			
			var file = evt.target.files[0];			
			var reader = new FileReader();
			var self = this;

			reader.readAsText(file);

			reader.onload = function() {
				$.ajax({
					url : "XMLImporter",
					type : "POST",
					data : reader.result,
					contentType : "application/json; charset=UTF-8",
					complete : function(e, xhr, settings) {					
						self.allowImport(e.status);
						self.validXML = reader.result;
					}
				});				
			};			
		},

		importXML : function() {
			var parser = app.XMLParser;
			
			parser.parseOMTGSchema(this.validXML);
			
			app.canvasView.updateHistory();
			
			this.teardown();
		},
		
		allowImport : function(status) {
			if(status == 202){
				this.$("#btnImport").removeClass("disabled");
			}
			else {
				this.$("#btnImport").addClass("disabled");
			}
		}
		
	});

})(jQuery);