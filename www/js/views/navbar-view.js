(function($) {
	'use strict';

	// Canvas View
	// ----------

	app.NavbarView = Backbone.View.extend({

		events : {
			'click #btnImportXML' : 'importXML',
			'click #btnExportXML' : 'exportXML',
			'click #btnExportSQL' : 'exportSQL',
			'click #btnExportPostgis' : 'exportPostgis',
			'click #btnPrint' : 'print',
			'click #btnAbout' : 'showAbout',
			
			'change #tgglGrid': 'changeGrid',
			'change #tgglShadow': 'changeDiagramShadow',
			'click #dropSettings' : 'dropSettingsClick',
		},

		importXML : function() {
			
			if(app.canvas.get('diagrams').length > 0){
				alert(app.msgs.NOT_EMPTY_PROJECT);
				return;
			}
			new app.XMLImporterView();					
		},		
		
		exportXML : function() {
			
			if(app.canvas.get('diagrams').length == 0){
				alert(app.msgs.EMPTY_PROJECT);
				return;
			}
			
			app.plumb.doWhileSuspended(function(){				
				
				var xml = app.canvas.toXML();
			
				var blob = new Blob([xml]);

				saveAs(blob, "OMTG.xml");
				
			}, false);
		},	
		
		exportSQL : function() {
			
			if(app.canvas.get('diagrams').length == 0){
				alert(app.msgs.EMPTY_PROJECT);
				return;
			}
			
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

						saveAs(blob, "OMTG-Oracle.zip");
					},
					error: function(ajaxrequest, ajaxOptions, thrownError)
					{
						alert("The request failed.");
					}
				});				

			}, false);
		},	
		
		exportPostgis : function() {
			
			if(app.canvas.get('diagrams').length == 0){
				alert(app.msgs.EMPTY_PROJECT);
				return;
			}
			
			app.plumb.doWhileSuspended(function(){				

				var xml = app.canvas.toXML();

				$.ajax({
					url : "omtg2postgis",
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

						saveAs(blob, "OMTG-Postgis.zip");
					},
					error: function(ajaxrequest, ajaxOptions, thrownError)
					{
						alert("The request failed.");
					}
				});				

			}, false);
		},	
		
		print : function() {
			app.canvasView.print();
		},
		
		showAbout : function() {
			new app.AboutView();
		},
		
		dropSettingsClick : function() {
			event.stopPropagation();

			if(event.target.nodeName == 'LABEL'){
				$(event.target).parent().parent().find('input').bootstrapToggle('toggle');
			}
			else{
				$(event.target).find('input').bootstrapToggle('toggle');
			}
		},
		
		changeGrid : function() {
			app.canvas.set('grid', $('#tgglGrid').prop('checked'));
		},
		
		changeDiagramShadow : function() {
			app.canvas.set('diagramShadow', $('#tgglShadow').prop('checked'));
		},
		
	});

})(jQuery);