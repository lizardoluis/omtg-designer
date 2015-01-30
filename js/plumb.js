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
//		return [ "PlainArrow", { 
//			length:25, 
//			width: 25, 
//			location: position, 
//			direction:-1, 
//			paintStyle:{
//				strokeStyle:"black",
//				fillStyle:color	            
//			},
//			id: "generalization-triangle",
//		}];	
		
		return ["Custom", {
        	create:function(component) {
        		return $('<svg id="generalization-triangle" class="generalization-triangle" height="26" width="26"><polygon points="1,25 13,1 25,25" stroke="black" stroke-width="1" fill="'+ color +'" /></svg>');                
        	},
        	location:position,
        	id: "generalization-triangle",
        }];	
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
        		return $('<svg id="cartographic-square" class="cartographic-square" width="16px" height="16px"><rect width="15px" height="15px"  stroke="black" stroke-width="2" fill="'+ color +'" /></svg>');                
        	},
        	location: position,    
        	id: "cartographic-square",
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
			connector: ["Flowchart", {stub: [50, 30], alwaysRespectStubs: true}],
			paintStyle : defaultConnectorStyle,
			overlays : [ triangle("white", 13) ],
//			parameters: {"type" : "generalization-disjoint-partial"},
		},
		"generalization-overlapping-partial" : {
			connector: ["Flowchart", {stub: [50, 30], alwaysRespectStubs: true}],
			paintStyle : defaultConnectorStyle,
			overlays : [ triangle("black", 13) ],
//			parameters: {"type" : "generalization-overlapping-partial"},
		},
		"generalization-disjoint-total" : {
			connector: ["Flowchart", {stub: [50, 30], alwaysRespectStubs: true}],
			paintStyle : defaultConnectorStyle,
			overlays : [circle(9), triangle("white", 30) ],
//			parameters: {"type" : "generalization-disjoint-total"},
		},
		"generalization-overlapping-total" : {
			connector: ["Flowchart", {stub: [50, 30], alwaysRespectStubs: true}],
			paintStyle : defaultConnectorStyle,
			overlays : [circle(9), triangle("black", 30) ],
//			parameters: {"type" : "generalization-overlapping-total"},
		},
		"generalization-leg" : {
			connector: ["Flowchart", {stub: [30, 30], alwaysRespectStubs: true}],
			paintStyle : defaultConnectorStyle,
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
			connector: ["Flowchart", {stub: [70, 50], alwaysRespectStubs: true}],
			paintStyle : dashedConnectorStyle,
			overlays : [square("white", 70), [ "Label", { label:"scale", location:0.5, cssClass: "cartographic-label" }]],
//			parameters: {"type" : "cartographic-generalization-disjoint"},
		},
		"cartographic-generalization-overlapping" : {
			connector: ["Flowchart", {stub: [70, 50], alwaysRespectStubs: true}],
			paintStyle : dashedConnectorStyle,
			overlays : [square("black", 70), [ "Label", { label:"scale", location:0.5, cssClass: "cartographic-label" }]],
//			parameters: {"type" : "cartographic-generalization-overlapping"},
		},
		"cartographic-leg" : {
			connector: ["Flowchart", {stub: [0, 50], alwaysRespectStubs: true}],
			paintStyle : dashedConnectorStyle,
		},
	});

	
	// This events checks which type of connection the user chose in the
	// toolbox, and set the type of the connection to the selected one.
	app.plumb.bind("connectionDrag", function(connection) {		
		
		var tool = app.canvas.get('activeTool');	
		if (tool && tool.get('model') == 'omtgRelation') {
			connection.setType(tool.get('name'));
			return;
		}		
		
		// if connection comes from a cartographic square, set type as cartographic-leg
		if(connection.sourceId == "cartographic-square"){
			connection.setType("cartographic-leg");
		}
		else if(connection.sourceId == "generalization-triangle"){
			connection.setType("generalization-leg");
		}
	});
		
	
	// This events it is used to set some properties, like specific
	// anchors, to some connections. Here the second line of the
	// arc-network connection is also connected.
	app.plumb.bind("connection", function (info, originalEvent) {
				
		var type = info.connection.getType()[0];
		
		switch(type){
		//Adds the second line in network relationships
		case "arc-network":
			var sibling = app.plumb.connect({
				source:info.sourceId, 
				target:info.targetId,
				type:"arc-network-sibling",
				parameters:{
					"sibling": info.connection
				}
			});
			info.connection.setParameter("sibling", sibling);
			break;
			
		case "generalization-disjoint-partial":
		case "generalization-disjoint-total":
		case "generalization-overlapping-partial":
		case "generalization-overlapping-total":
			// Set connection anchors, due to the drag
			// change of anchors, default in jsplumb, the
			// connection must be detached and re-atached
			// with the Bottom and Top anchors
			app.plumb.detach(info.connection);
			var newConn = app.plumb.connect({
				source : info.connection.sourceId,
				target : info.connection.targetId,
				anchors : [ "Bottom", "Top" ],
				type : type,
				fireEvent: false // avoids connect event loop
			});
			
			// Make generalization overlay a source of more connections
			app.newconn = newConn;
			app.plumb.makeSource(newConn.getOverlay("generalization-triangle").getElement(),{
				anchor: [ 0, 0.5, 0, 1 ],
//				parameters: {"sourceDiagramID" : info.connection.sourceId},
			});	
			break;
			
		// Generalization leg type connection with top target anchor
		case "generalization-leg":
			info.connection.endpoints[1].setAnchor("Top");
			break;
			
		case "cartographic-generalization-disjoint":
		case "cartographic-generalization-overlapping":
			// Set connection anchors, due to the drag
			// change of anchors, default in jsplumb, the
			// connection must be detached and re-atached
			// with the Bottom and Top anchors
			app.plumb.detach(info.connection);
			var newConn = app.plumb.connect({
				source : info.connection.sourceId,
				target : info.connection.targetId,
				anchors : [ "Bottom", "Top" ],
				type : type,
				fireEvent: false  // avoids this event loop
			});
			
			// Make the middle square of the connection a source of connections
			app.plumb.makeSource(newConn.getOverlay("cartographic-square").getElement());	
			break;
			
		// Cartographic leg type connection with top target anchor
		case "cartographic-leg":
			info.connection.endpoints[1].setAnchor("Top");
			break;
		}
	});
	

	// Event that checks for relationship restrictions. Before
	// dropping a connection to its target, the Designer checks if
	// this relationship is valid accordingly to OMT-G restrictions.
	app.plumb.bind("beforeDrop", function(info) {
		
		// Avoid self loop. 
		// TODO: support network self loop
		if(info.sourceId == info.targetId)
			return false;

		// Get type of the connection and type of the diagrams
		var type = info.connection.getType()[0];
		var sourceType = app.canvas.get('diagrams').getType(info.sourceId);
		var targetType = app.canvas.get('diagrams').getType(info.targetId);
				
		switch(type){		
		// Superclass and subclasses must have the same type
		case "generalization-disjoint-partial":
		case "generalization-overlapping-partial":
		case "generalization-disjoint-total":
		case "generalization-overlapping-total":
			return sourceType == targetType;
			
		// Aggregation must be between conventional classes	
		case "aggregation":
			return sourceType == targetType == "conventional";			

		// Superclass must be conventional and subclasses georeferenced
		// TODO: conceptual legs cannot connect conventional classes 
		case "cartographic-generalization-disjoint":
		case "cartographic-generalization-disjoint":
			return (sourceType == "conventional") && (targetType != "conventional");
			
		case "cartographic-leg":
			return targetType != "conventional";
			
		// Both classes must be georeferenced	
		case "spatial-association":
		case "spatial-aggregation":
			return (sourceType != "conventional") && (targetType != "conventional")
			
		// If source == target, they must bi bi-line or un-line.
		// if source or target is a node, the other side must be bi-line or un-line.
		case "arc-network":			
			return (sourceType == targetType && (sourceType == "bi-line" || sourceType == "un-line")) ||
			(sourceType == "node" && (targetType == "bi-line" || targetType == "un-line")) ||
			(targetType == "node" && (sourceType == "bi-line" || sourceType == "un-line"));
		}
		
		return true;		
	});
	
	app.plumb.bind("dblclick", function(conn, originalEvent) {

//		var type = conn.getType()[0];
		
		var modal = new app.omtg.ConnectionEditorView();
//		var modal = new app.omtg.DiagramEditorView({model : new app.omtg.Diagram()});
//		console.log()
	});

});

function(open)
