
jsPlumb.ready(function() {

	var defaultConnectorStyle = { 
			strokeStyle:"black", 
			lineWidth:2, 
			outlineColor:"transparent", 
			outlineWidth:4
	};

	var dashedConnectorStyle = { 
			strokeStyle:"black", 
			lineWidth:2, 
			outlineColor:"transparent", 
			outlineWidth:4, 
			dashstyle:"4" 
	};

	var connectorHoverStyle = {
			lineWidth:4,
			strokeStyle:"#1e8151",
			outlineWidth:2,
			outlineColor:"white"
	};	
	
	var diamondOverlay = [ [ "Diamond", {
		length : 35,
		width : 18,
		location : 35,
		paintStyle : {
			strokeStyle : "black",
			fillStyle : "white"
		}
	} ] ];
	
	// Plumbing setup
	app.plumb = jsPlumb.getInstance({
		Anchor : "Continuous",
		ConnectionsDetachable : false,
		Connector : "Flowchart",
		Container : "canvas",

		DragOptions : {
			cursor : "pointer",
			zIndex : 2000
		},

		Endpoint : "Blank",
	});

	app.plumb.registerConnectionTypes({
		"association" : {
			paintStyle : defaultConnectorStyle,
			hoverPaintStyle : connectorHoverStyle,
		},
		"spatial-association" : {
			paintStyle : dashedConnectorStyle,
			hoverPaintStyle : connectorHoverStyle,
		},
		"aggregation" : {
			paintStyle : defaultConnectorStyle,
			hoverPaintStyle : connectorHoverStyle,
			overlays : diamondOverlay,
		},
		"spatial-aggregation" : {
			paintStyle : dashedConnectorStyle,
			hoverPaintStyle : connectorHoverStyle,
			overlays : diamondOverlay,
		},
	});

	app.plumb.bind("connectionDrag", function(connection) {
		var tool = app.canvas.get('activeTool');	
		if (tool && tool.get('model') == 'omtgRelation') {
			connection.setType(tool.get('name'));
		}		
	});

});
