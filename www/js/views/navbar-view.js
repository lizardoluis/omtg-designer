(function($) {
	'use strict';

	app.NavbarView = Backbone.View.extend({

		events : {
			'click #btnImportXML' : 'importXML',
			'click #btnExportXML' : 'exportXML',
			'click #btnExportSQL' : 'exportSQL',
			'click #btnExportAstPostgis' : 'exportAstPostgis',
			'click #btnExportPostgis' : 'exportPostgis',
			'click #btnPrint' : 'print',
			'click #btnAbout' : 'showAbout',
			'input #projectNameInput' : 'onProjectNameInput',
			'blur #projectNameInput' : 'onProjectNameBlur',
			'keydown #projectNameInput' : 'onProjectNameKeydown',
			
			'change #tgglGrid': 'changeGrid',
			'change #tgglShadow': 'changeDiagramShadow',
			'change #tgglSnapToGrid': 'changeSnapToGrid',
			'click #dropSettings' : 'dropSettingsClick',
		},

		onProjectNameInput : function() {
			var name = $('#projectNameInput').val().trim();
			app.canvas.set('projectName', name);
			document.title = name ? name + ' \u2013 OMT-G Designer' : 'OMT-G Designer';
		},

		onProjectNameBlur : function() {
			var name = $('#projectNameInput').val().trim();
			$('#projectNameInput').val(name);
			app.canvas.set('projectName', name);
			document.title = name ? name + ' \u2013 OMT-G Designer' : 'OMT-G Designer';
		},

		onProjectNameKeydown : function(e) {
			if (e.which === ENTER_KEY) {
				$('#projectNameInput').blur();
			}
		},

		_ensureProjectName : function() {
			var name = app.canvas.get('projectName');
			if (!name) {
				name = prompt('Enter a project name before exporting:', '');
				if (name === null) return null;
				name = name.trim() || 'Untitled-Project';
				app.canvas.set('projectName', name);
				$('#projectNameInput').val(name);
				document.title = name + ' \u2013 OMT-G Designer';
			}
			return name;
		},

		_sanitizeForFileName : function(name) {
			return name.replace(/[^a-zA-Z0-9_-]/g, '-').replace(/-+/g, '-').replace(/^-|-$/g, '');
		},

		_getTimestamp : function() {
			var now = new Date();
			var pad = function(n) { return n < 10 ? '0' + n : '' + n; };
			return now.getFullYear() + pad(now.getMonth() + 1) + pad(now.getDate())
				+ '-' + pad(now.getHours()) + pad(now.getMinutes()) + pad(now.getSeconds());
		},

		_buildFileName : function(projectName, dbName, extension) {
			var safeName = this._sanitizeForFileName(projectName);
			var ts = this._getTimestamp();
			if (dbName) {
				return 'OMTG-' + safeName + '-' + dbName + '-' + ts + '.' + extension;
			}
			return 'OMTG-' + safeName + '-' + ts + '.' + extension;
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

			var projectName = this._ensureProjectName();
			if (!projectName) return;
			var fileName = this._buildFileName(projectName, null, 'xml');
			
			app.plumb.doWhileSuspended(function(){				
				var xml = app.canvas.toXML();
				var blob = new Blob([xml]);
				saveAs(blob, fileName);
			}, false);
		},	
		
		exportSQL : function() {
			
			if(app.canvas.get('diagrams').length == 0){
				alert(app.msgs.EMPTY_PROJECT);
				return;
			}

			var projectName = this._ensureProjectName();
			if (!projectName) return;
			var fileName = this._buildFileName(projectName, 'Oracle', 'zip');
			
			app.plumb.doWhileSuspended(function(){				
				var xml = app.canvas.toXML();
				var xhr = new XMLHttpRequest();
				
				xhr.open("POST", "omtg2sql", true);
				xhr.setRequestHeader("Content-type","application/json");
				xhr.setRequestHeader("Access-Control-Allow-Origin", "*");
				
				xhr.onreadystatechange = function() {
				    if (xhr.readyState == 4 && xhr.status == 200) {				  
				        var blob = new Blob([xhr.response], {type: "octet/stream"});
				        saveAs(blob, fileName);
				    }
				}
				
				xhr.responseType = "arraybuffer";
				xhr.send(xml);
				
			}, false);
		},	
		
		exportAstPostgis : function() {
			
			if(app.canvas.get('diagrams').length == 0){
				alert(app.msgs.EMPTY_PROJECT);
				return;
			}

			var projectName = this._ensureProjectName();
			if (!projectName) return;
			var fileName = this._buildFileName(projectName, 'AstPostgis', 'zip');
			
			app.plumb.doWhileSuspended(function(){				
				var xml = app.canvas.toXML();
				var xhr = new XMLHttpRequest();
				
				xhr.open("POST", "omtg2astpostgis", true);
				xhr.setRequestHeader("Content-type","application/json");
				xhr.setRequestHeader("Access-Control-Allow-Origin", "*");
				
				xhr.onreadystatechange = function() {
				    if (xhr.readyState == 4 && xhr.status == 200) {				  
				        var blob = new Blob([xhr.response], {type: "octet/stream"});
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

			var projectName = this._ensureProjectName();
			if (!projectName) return;
			var fileName = this._buildFileName(projectName, 'PostGIS', 'zip');
			
			app.plumb.doWhileSuspended(function(){				
				var xml = app.canvas.toXML();
				var xhr = new XMLHttpRequest();
				
				xhr.open("POST", "omtg2postgis", true);
				xhr.setRequestHeader("Content-type","application/json");
				xhr.setRequestHeader("Access-Control-Allow-Origin", "*");
				
				xhr.onreadystatechange = function() {
				    if (xhr.readyState == 4 && xhr.status == 200) {				  
				        var blob = new Blob([xhr.response], {type: "octet/stream"});
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