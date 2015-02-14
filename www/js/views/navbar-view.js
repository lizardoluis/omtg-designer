(function($) {
	'use strict';

	// Canvas View
	// ----------

	app.NavbarView = Backbone.View.extend({

		events : {
			'click #btnImportXML' : 'importXML',
			'click #btnExportXML' : 'exportXML',
			'click #btnExportSQL' : 'exportSQL',
			'click #btnPrint' : 'print',
		},

		importXML : function() {
			new app.XMLImporterView();
		},		
		
		exportXML : function() {
			app.plumb.doWhileSuspended(function(){				
				
				var xml = app.canvas.toXML();
			
				var blob = new Blob([xml]);

				saveAs(blob, "OMTG.xml");
				
			}, false);
		},	
		
		exportSQL : function() {
			
			app.plumb.doWhileSuspended(function(){				

				var xml = app.canvas.toXML();

				$.ajax({
					url : "omtg2sql",
					type: "POST",
					data : xml,
					contentType : "application/json; charset=UTF-8",
					success: function(data, textStatus, jqXHR)
					{
						var byteArray = new Uint8Array(data.length);
						for (var i = 0; i < data.length; i++) {
							byteArray[i] = data.charCodeAt(i) & 0xff;
						}
						var blob = new Blob([byteArray]);

						saveAs(blob, "OMTG.zip");
					},
					error: function(ajaxrequest, ajaxOptions, thrownError)
					{
						alert("The request failed.");
					}
				});				

			}, false);
		},	
		
		print : function() {
			app.canvas.get('diagrams').unselectAll();
		},	
	});

})(jQuery);