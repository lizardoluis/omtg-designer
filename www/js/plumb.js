jsPlumb.ready(function() {

	var defaultConnectorStyle = { 
			stroke:"black", 
			strokeWidth:2
	};

	var dashedConnectorStyle = { 
			stroke:"black", 
			strokeWidth:2,
			"dashstyle": "4 3" 
	};

	var connectorHoverStyle = {
			stroke:"#1e8151",
			strokeWidth:4,
			outlineStroke:"transparent", 
			outlineWidth:2
	};	
	
	var diamondOverlay = [ [ "Diamond", {
		length : 35,
		width : 18,
		location : 35,
		paintStyle : {
			stroke : "black",
			fill : "white"
		}
	} ] ];
	
	var triangleEndpoint = function(type){ 
		if(type == "generalization-disjoint-partial")
			return ["Image", {src:"imgs/omtg/triangle-white.png", cssClass:"triangle-endpoint"}];
		if(type == "generalization-overlapping-partial")
			return ["Image", {src:"imgs/omtg/triangle-black.png", cssClass:"triangle-endpoint"}];
		if(type == "generalization-disjoint-total")
			return ["Image", {src:"imgs/omtg/triangle-circle-white.png", cssClass:"triangle-endpoint"}];
		if(type == "generalization-overlapping-total")
			return ["Image", {src:"imgs/omtg/triangle-circle-black.png", cssClass:"triangle-endpoint"}];
	};

	var squareOverlay = function(type){ 
		if(type == "cartographic-generalization-overlapping")
			return ["Image", {src:"imgs/omtg/square-black.png", cssClass:"square-overlay"}];		
		if(type == "cartographic-generalization-disjoint")
			return ["Image", {src:"imgs/omtg/square-white.png", cssClass:"square-overlay"}];
	};
	
	
	var square = function(color, position){
		return ["Custom", {
	    	create: function(component) {
	    		return $('<img class="cartographic-square" src="imgs/omtg/square-'+ color +'.png" alt="'+ color +' square" width="16px" height="16px" >');                
	    	},
	    	id: "cartographic-square",
	    	cssClass: "cartographic-square",
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
		Endpoint : "Blank"
	});
	
	
	// Define all the connection types
	app.plumb.registerConnectionTypes({
		"association" : {
			paintStyle: defaultConnectorStyle,
			hoverPaintStyle: connectorHoverStyle,
			overlays : [[ "Label", { label:"", location:0.5, id:"description-label", cssClass: "description-label" } ],
			            [ "Label", { label:"0..*", location:45, id:"cardinality-labelA", cssClass: "cardinality-label" } ],
			            [ "Label", { label:"0..*", location:-45, id:"cardinality-labelB", cssClass: "cardinality-label" } ]],
			parameters: {
				"minA" : "0",
				"maxA" : "*",
				"minB" : "0",
				"maxB" : "*",
			},
		},
		"spatial-association" : {
			paintStyle: dashedConnectorStyle,
			hoverPaintStyle: connectorHoverStyle,
			overlays : [[ "Label", { label:"Intersects", location:0.5, id:"description-label", cssClass: "description-label" } ],
			            [ "Label", { label:"0..*", location:45, id:"cardinality-labelA", cssClass: "cardinality-label" } ],
			            [ "Label", { label:"0..*", location:-45, id:"cardinality-labelB", cssClass: "cardinality-label" } ]],
			parameters: {
				"minA" : "0",
				"maxA" : "*",
				"minB" : "0",
				"maxB" : "*",
				"distance" : "",
			},
		},
		"aggregation" : {
			paintStyle : defaultConnectorStyle,
			hoverPaintStyle: connectorHoverStyle,
			overlays : diamondOverlay,
		},
		"spatial-aggregation" : {
			paintStyle : dashedConnectorStyle,
			hoverPaintStyle: connectorHoverStyle,
			overlays : diamondOverlay,
		},
		"generalization-disjoint-partial" : {
			connector: ["Flowchart", {stub: [50, 30], alwaysRespectStubs: true}],
			paintStyle : defaultConnectorStyle,
			hoverPaintStyle: connectorHoverStyle,
			parameters:{
				"participation":"partial",
				"disjointness":"disjoint",
			},
		},
		"generalization-overlapping-partial" : {
			connector: ["Flowchart", {stub: [50, 30], alwaysRespectStubs: true}],
			paintStyle : defaultConnectorStyle,
			hoverPaintStyle: connectorHoverStyle,
			parameters:{
				"participation":"partial",
				"disjointness":"overlapping",
			},
		},
		"generalization-disjoint-total" : {
			connector: ["Flowchart", {stub: [50, 30], alwaysRespectStubs: true}],
			paintStyle : defaultConnectorStyle,
			hoverPaintStyle: connectorHoverStyle,
			parameters:{
				"participation":"total",
				"disjointness":"disjoint",
			},
		},
		"generalization-overlapping-total" : {
			connector: ["Flowchart", {stub: [50, 30], alwaysRespectStubs: true}],
			paintStyle : defaultConnectorStyle,
			hoverPaintStyle: connectorHoverStyle,
			parameters:{
				"participation":"total",
				"disjointness":"overlapping",
			},
		},
		"generalization-leg" : {
			connector: ["Flowchart", {stub: [50, 30], alwaysRespectStubs: true}],
			paintStyle : defaultConnectorStyle,
			hoverPaintStyle: connectorHoverStyle,
		},
		"arc-network" : {
			paintStyle : dashedConnectorStyle,
			hoverPaintStyle: connectorHoverStyle,
			overlays : [[ "Label", { label:"network", location:0.5, id:"description-label", cssClass: "arc-network-label" } ]],
		},
		"arc-network-sibling" : {
			paintStyle : dashedConnectorStyle,
			hoverPaintStyle: connectorHoverStyle,
		},		
		"arc-network-self" : {
			paintStyle : dashedConnectorStyle,
			hoverPaintStyle: connectorHoverStyle,
			overlays : [[ "Label", { label:"network", location:0.4, id:"description-label", cssClass: "arc-network-label" } ]],
		},
		"arc-network-sibling-self" : {
			paintStyle : dashedConnectorStyle,
			hoverPaintStyle: connectorHoverStyle,
		},
		"cartographic-generalization-disjoint" : {
			paintStyle : dashedConnectorStyle,
			hoverPaintStyle: connectorHoverStyle,
			overlays : [square("white", 70), [ "Label", { label:"scale", location:0.5, id: "cartographic-label", cssClass: "cartographic-label" }]],
			parameters:{
				"disjointness":"disjoint"
			},
		},
		"cartographic-generalization-overlapping" : {
			paintStyle : dashedConnectorStyle,
			hoverPaintStyle: connectorHoverStyle,
			overlays : [square("black", 70), [ "Label", { label:"scale", location:0.5, id: "cartographic-label", cssClass: "cartographic-label" }]],
			parameters:{
				"disjointness":"overlapping",
			},
		},
		"cartographic-leg" : {
			paintStyle : dashedConnectorStyle,
			hoverPaintStyle: connectorHoverStyle,
		},
	});
	
	// This events checks which type of connection the user chose in the
	// toolbox, and set the type of the connection to the selected one.
	app.plumb.bind("connectionDrag", function(connection) {		
		
		var tool = app.canvas.get('activeTool');	
		if (tool != null && tool.get('model') == 'omtgRelation') {
			var type = tool.get('name');			
			connection.setType(type);
			
			// set connector to arc-network
			if(type == "arc-network")
				connection.setConnector("Straight");
			
			return;
		}		
		
		// if connection comes from a cartographic square, set type as cartographic-leg
		if(connection.source.classList.contains("cartographic-square")){
			connection.setType("cartographic-leg");
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
			
			if(info.connection.sourceId == info.connection.targetId){
				app.plumb.detach(info.connection);

				var newConn = app.plumb.connect({
					source : info.connection.sourceId,
					target : info.connection.targetId,
					anchors : [ [ 0.35, 1, 0, 1 ], [ 1, 0.5, 1, 0 ] ]
				});	
				newConn.setType("arc-network-self");

				var sibling = app.plumb.connect({
					source: info.connection.sourceId, 
					target: info.connection.targetId,
					anchors : [ [ 0.5, 1, 0, 1 ], [ 1, 0.75, 1, 0 ] ]
				});				
				sibling.setType("arc-network-sibling-self");
				
				// set parameters
				sibling.setParameter("sibling", newConn);
				newConn.setParameter("sibling", sibling);
				
				// set connectors
				newConn.setConnector(["Flowchart", {stub: [50, 50], alwaysRespectStubs: true}]);
				sibling.setConnector(["Flowchart", {stub: [25, 25], alwaysRespectStubs: true}]);
			}
			else{						
				var sibling = app.plumb.connect({
					source:info.connection.sourceId, 
					target:info.connection.targetId
				});						
				sibling.setType("arc-network-sibling");
				
				// set parameters
				sibling.setParameter("sibling", info.connection);
				info.connection.setParameter("sibling", sibling);	
				
				// set connectors
				info.connection.setConnector("Straight");
				sibling.setConnector("Straight");
			}
		
			break;
			
		case "generalization-disjoint-partial":
		case "generalization-disjoint-total":
		case "generalization-overlapping-partial":
		case "generalization-overlapping-total":
						
			var participation = info.connection.getParameter("participation");
			var disjointness = info.connection.getParameter("disjointness");
			
			// Detach the connection to add a new one with the correct
			// endpoint
			app.plumb.detach(info.connection);
			
			// Adds the endpoint to the diagram
			var endpoint = app.plumb.addEndpoint(info.connection.sourceId, {
				anchor : [ 0.5, 1.2, 0, 1 ],
				connectionType : "generalization-leg",
				endpoint : triangleEndpoint(type),
				isSource : true,
				isTarget : false,
				maxConnections : 100,
				uniqueEndpoint : true,				
				parameters:{
					"participation": participation,
					"disjointness": disjointness,
			    }
			});
			
			// Re-atach the connection, but now to the endpoint
			var newConn = app.plumb.connect({
				anchors : [ "Bottom", "Top" ],
				source : endpoint,
				target : info.connection.targetId
			});	
			newConn.setType(type);
			
			// Remove the overlay of the connection, added by the type
			newConn.removeAllOverlays();
			
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
				anchors : [ "Bottom", "Top" ]
			});
			newConn.setConnector(["Flowchart", {stub: [70, 50], alwaysRespectStubs: true}]);
			newConn.setType(type);
			
			// Make the middle square of the connection a source of connections
			app.plumb.makeSource(newConn.getOverlay("cartographic-square").getElement());	
			break;
			
		// Cartographic leg type connection with top target anchor
		case "cartographic-leg":
			info.connection.endpoints[1].setAnchor("Top");
			info.connection.setConnector(["Flowchart", {stub: [0, 50], alwaysRespectStubs: true}]);
			break;
		} 
	});
	

	// Event that checks for relationship restrictions. Before
	// dropping a connection to its target, the Designer checks if
	// this relationship is valid accordingly to OMT-G restrictions.
	app.plumb.bind("beforeDrop", function(info) {
		
		// Get type of the connection and type of the diagrams
		var type = info.connection.getType()[0];
		
		// Avoid self loop. 
		if(type != "arc-network" && info.sourceId == info.targetId){
			return false;
		}

		var sourceType = app.canvas.get('diagrams').get(info.sourceId, 'type');
		var targetType = app.canvas.get('diagrams').get(info.targetId, 'type');
				
		switch(type){		
		// Superclass and subclasses must have the same type
		case "generalization-disjoint-partial":
		case "generalization-overlapping-partial":
		case "generalization-disjoint-total":
		case "generalization-overlapping-total":
			return sourceType == targetType;
			
		// Aggregation must be between conventional classes	
		case "aggregation":
			return (sourceType == "conventional") || (targetType == "conventional");			

		// Superclass must be conventional and subclasses georeferenced
		case "cartographic-generalization-disjoint":
		case "cartographic-generalization-overlapping":
			return (sourceType == "conventional") && (targetType != "conventional");
			
		case "cartographic-leg":
			return targetType != "conventional";
			
		// Both classes must be georeferenced	
		case "spatial-association":
		case "spatial-aggregation":
			return (sourceType != "conventional") && (targetType != "conventional");
			
		// If source == target, they must bi bi-line or un-line.
		// if source or target is a node, the other side must be bi-line or un-line.
		case "arc-network":			
			return (sourceType == targetType && (sourceType == "node" || sourceType == "bi-line" || sourceType == "un-line")) ||
			(sourceType == "node" && (targetType == "bi-line" || targetType == "un-line")) ||
			(targetType == "node" && (sourceType == "bi-line" || sourceType == "un-line"));
		}
		
		return true;		
	});
	
	
	// Event that opens modal for edit the connection or to delete it
	app.plumb.bind("dblclick", function(conn, originalEvent) {
		
		var param = conn;
				
		// Fix the bug of clicking an overlay
		if(conn instanceof jsPlumb.Overlays.Label)
			param = conn.component;
		
		// For arc-network when clicked in the sibling connection
		var type = param.getType()[0];
		if(type == 'arc-network-sibling' || type == 'arc-network-sibling-self')
			param = param.getParameter('sibling');

		
		// Connection types without attributes
		// to be edited or without legs. Only
		// opens a confirmation dialog for
		// deletion.
		if (type == 'aggregation'
			|| type == 'spatial-aggregation'
					|| type == 'cartographic-leg') {

			if (confirm(app.msgs.DELETE_CONNECTION)){
				app.plumb.detach(param);
			}
		}
		
		else if (type == "generalization-disjoint-partial"
			|| type == "generalization-disjoint-total"
				|| type == "generalization-overlapping-partial"
					|| type == "generalization-overlapping-total") {
			
			if (confirm(app.msgs.DELETE_CONNECTION)){
				
				var endpoint = param.endpoints[0];
				app.plumb.detach(param);
				
				// Delete the endpoint triangle if there are no connections anymore.
				if(endpoint.getAttachedElements().length == 0){
					app.plumb.deleteEndpoint(endpoint);
				}
				else{
					endpoint.getAttachedElements()[0].setType(type);
					endpoint.getAttachedElements()[0].removeAllOverlays();
				}
			}	
		}
		
		// Generalization connections. 
		else if(type == 'generalization-leg'){
			
			if (confirm(app.msgs.DELETE_CONNECTION)){
				
				var endpoint = param.endpoints[0];
				app.plumb.detach(param);
				
				// Delete the endpoint triangle if there are no connections anymore.
				if(endpoint.getAttachedElements().length == 0){
					app.plumb.deleteEndpoint(endpoint);
				}
			}			
		}	
		
		// Connections with attributes to be edited. Opens the editor modal
		else {
			new app.omtg.ConnectionEditorView({connection : param});
		}		
	});
});
