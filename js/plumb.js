
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
	
	var triangle = function(color, position){
		return [ "PlainArrow", { 
			length:25, 
			width: 25, 
			location: position, 
			direction:-1, 
			paintStyle:{
				strokeStyle:"black",
				fillStyle:color	            
			} 
		} ];	
	}
	
	var circle = function(position){
		return ["Custom", {
        	create:function(component) {
        		return $('<svg width="16px" height="16px"><circle cx="8" cy="8" r="8" stroke="black" stroke-width="0" fill="black" /></svg>');                
        	},
        	location:position,
        }];	
	};
	
	var square = function(color, position){
		return ["Custom", {
        	create: function(component) {
        		return $('<svg width="16px" height="16px"><rect width="15px" height="15px"  stroke="black" stroke-width="2" fill="'+ color +'" /></svg>');                
        	},
        	id: "cartographic-square",
        	location: position,        	
        }];	
	};	
	
	// Plumbing default setup
	app.plumb = jsPlumb.getInstance({
		Anchor : "Continuous",
		ConnectionsDetachable : false,
		Connector : "Flowchart",
		Container : "canvas",
		DragOptions : {cursor : "pointer", zIndex : 2000},
		Endpoint : "Blank",
		HoverPaintStyle: connectorHoverStyle,
	});

	app.plumb.registerConnectionTypes({
		"association" : {
			paintStyle : defaultConnectorStyle,
//			parameters: {"type" : "association"},
		},
		"spatial-association" : {
			paintStyle : dashedConnectorStyle,
//			parameters: {"type" : "spatial-association"},
		},
		"aggregation" : {
			paintStyle : defaultConnectorStyle,
			overlays : diamondOverlay,
//			parameters: {"type" : "aggregation"},
		},
		"spatial-aggregation" : {
			paintStyle : dashedConnectorStyle,
			overlays : diamondOverlay,
//			parameters: {"type" : "spatial-aggregation"},
		},
		"generalization-disjoint-partial" : {
			anchors : [ "Bottom", "Top" ],
			connector: ["Flowchart", {stub: 50, alwaysRespectStubs: true}],
			paintStyle : defaultConnectorStyle,
			overlays : [ triangle("white", 2) ],
//			parameters: {"type" : "generalization-disjoint-partial"},
		},
		"generalization-overlapping-partial" : {
			connector: ["Flowchart", {stub: 50, alwaysRespectStubs: true}],
			paintStyle : defaultConnectorStyle,
			overlays : [ triangle("black", 2) ],
//			parameters: {"type" : "generalization-overlapping-partial"},
		},
		"generalization-disjoint-total" : {
			connector: ["Flowchart", {stub: 50, alwaysRespectStubs: true}],
			paintStyle : defaultConnectorStyle,
			overlays : [circle(9), triangle("white", 17) ],
//			parameters: {"type" : "generalization-disjoint-total"},
		},
		"generalization-overlapping-total" : {
			connector: ["Flowchart", {stub: 50, alwaysRespectStubs: true}],
			paintStyle : defaultConnectorStyle,
			overlays : [circle(9), triangle("black", 17) ],
//			parameters: {"type" : "generalization-overlapping-total"},
		},
		"arc-network" : {
			connector: "Straight",
			paintStyle : dashedConnectorStyle,
			overlays : [[ "Label", { label:"network", location:0.5, cssClass: "arc-network-label" } ]],
//			parameters: {"type" : "arc-network"},
		},
		"arc-network-sibling" : {
			connector: "Straight",
			paintStyle : dashedConnectorStyle,
//			parameters: {"type" : "arc-network-sibling"},
		},
		"cartographic-generalization-disjoint" : {
			connector: ["Flowchart", {stub: 70, alwaysRespectStubs: true}],
			paintStyle : dashedConnectorStyle,
			overlays : [square("white", 70)/*, [ "Label", { label:"scale", location:0.5, cssClass: "cartographic-label" }]*/],
//			parameters: {"type" : "cartographic-generalization-disjoint"},
		},
		"cartographic-generalization-overlapping" : {
			connector: ["Flowchart", {stub: 70, alwaysRespectStubs: true}],
			paintStyle : dashedConnectorStyle,
			overlays : [square("black", 70), [ "Label", { label:"scale", location:0.5, cssClass: "cartographic-label" }]],
//			parameters: {"type" : "cartographic-generalization-overlapping"},
		},
	});

	app.plumb.bind("connectionDrag", function(connection) {
//		console.log("drag");
		var tool = app.canvas.get('activeTool');	
		if (tool && tool.get('model') == 'omtgRelation') {
			connection.setType(tool.get('name'));
		}		
	});
		
	app.plumb.bind("connection", function (info, originalEvent) {
		var type = info.connection.getType();

		//Adds the second line in network relationships
		if(type == "arc-network"){		
			var sibling = app.plumb.connect({
				source:info.sourceId, 
				target:info.targetId,
				type:"arc-network-sibling",
				parameters:{
					"sibling": info.connection
				}
			});
			info.connection.setParameter("sibling", sibling);
		}
		else if (type == "generalization-disjoint-partial"
			|| type == "generalization-disjoint-total"
				|| type == "generalization-overlapping-partial"
					|| type == "generalization-overlapping-total") {

			// Set connection anchors, due to the drag
			// change of anchors, default in jsplumb, the
			// connection must be detached and re-atached
			// with the Bottom and Top anchors
			app.plumb.detach(info.connection);
			app.plumb.connect({
				source : info.connection.sourceId,
				target : info.connection.targetId,
				anchors : [ "Bottom", "Top" ],
				endpoint : "Blank",
				type : type,
			});
		}
		else if (type == "cartographic-generalization-disjoint"
			|| type == "cartographic-generalization-overlapping") {

			// Set connection anchors, due to the drag
			// change of anchors, default in jsplumb, the
			// connection must be detached and re-atached
			// with the Bottom and Top anchors
			app.plumb.detach(info.connection);
			var newConn = app.plumb.connect({
				source : info.connection.sourceId,
				target : info.connection.targetId,
				anchors : [ "Bottom", "Top" ],
				endpoint : "Blank",
				type : type,
			});
			
			// Make the middle square of the connection a source of connections
			app.plumb.makeSource(newConn.getOverlay("cartographic-square").getElement(), {
				connector: ["Flowchart", {stub: 20}],
				connectorStyle: dashedConnectorStyle,
			});	

			console.log(newConn.getOverlay("cartographic-square"));
		}
		else if (type == "cartographic-leg"){
			app.con = info.connection;
			info.connection.endpoints[1].setAnchor("Top");
		}
	});
	
	app.plumb.bind("overlayMoved", function(connection, event) {
		console.log("moved");		
	});

});
