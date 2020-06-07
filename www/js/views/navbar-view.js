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
			'change #tgglSnapToGrid': 'changeSnapToGrid',
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
				var xhr = new XMLHttpRequest();
				
				xhr.open("POST", "omtg2sql", true);
				xhr.setRequestHeader("Content-type","application/json");
				xhr.setRequestHeader("Access-Control-Allow-Origin", "*");
				
				xhr.onreadystatechange = function() {
				    if (xhr.readyState == 4 && xhr.status == 200) {				  
				        var blob = new Blob([xhr.response], {type: "octet/stream"});
				        var fileName = "OMTG-Oracle.zip";
				        saveAs(blob, fileName);
				    }
				}
				
				xhr.responseType = "arraybuffer";
				xhr.send(xml);
				
			}, false);
		},	
		
		exportPostgis : function() {
			
			if(app.canvas.get('diagrams').length == 0){
				alert(app.msgs.EMPTY_PROJECT);
				return;
			}
			
			app.plumb.doWhileSuspended(function(){				

				var xml = app.canvas.toXML();
				var xhr = new XMLHttpRequest();
				
				xhr.open("POST", "omtg2postgis", true);
				xhr.setRequestHeader("Content-type","application/json");
				xhr.setRequestHeader("Access-Control-Allow-Origin", "*");
				
				xhr.onreadystatechange = function() {
				    if (xhr.readyState == 4 && xhr.status == 200) {				  
				        var blob = new Blob([xhr.response], {type: "octet/stream"});
				        var fileName = "OMTG-Postgis.zip";
				        saveAs(blob, fileName);
				    }
				}
				
				xhr.responseType = "arraybuffer";
				xhr.send(xml);

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
			if(event.target.nodeName == 'LABEL' || event.target.nodeName == 'SPAN'){
				$(event.target).parent().parent().find('input').bootstrapToggle('toggle');
			}
			else if(event.target.nodeName == 'A'){
				$(event.target).find('input').bootstrapToggle('toggle');
			}
		},
		
		changeGrid : function() {
			app.canvas.set('grid', $('#tgglGrid').prop('checked'));
		},
		
		changeDiagramShadow : function() {
			app.canvas.set('diagramShadow', $('#tgglShadow').prop('checked'));
		},
		
		changeSnapToGrid : function() {
			if($('#tgglSnapToGrid').prop('checked')){
				app.canvas.set('snapToGrid', 10);
			}
			else{
				app.canvas.set('snapToGrid', 1);
			}
		},
		
	});

})(jQuery);