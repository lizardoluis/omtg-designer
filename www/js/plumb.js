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

	
	var triangle = function(color, position){
		return ["Custom", {
        	create:function(component) {
        		return $('<svg class="generalization-triangle" height="26" width="26"><polygon points="1,25 13,1 25,25" stroke="black" stroke-width="1" fill="'+ color +'" /></svg>');                
        	},
        	location:position,
        	id: "generalization-triangle",
        	
        }];	
	};
	
	
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
        		return $('<svg class="cartographic-square" width="16px" height="16px"><rect width="15px" height="15px"  stroke="black" stroke-width="2" fill="'+ color +'" /></svg>');                
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
	
	
	// Define all the connection types
	app.plumb.registerConnectionTypes({
		"association" : {
			paintStyle : defaultConnectorStyle,
			overlays : [[ "Label", { label:"", location:0.5, id:"description-label", cssClass: "description-label" } ],
			            [ "Label", { label:"", location:45, id:"cardinality-labelA", cssClass: "cardinality-label" } ],
			            [ "Label", { label:"", location:-45, id:"cardinality-labelB", cssClass: "cardinality-label" } ]],
			parameters: {
				"minA" : "",
				"maxA" : "",
				"minB" : "",
				"maxB" : "",
			},
		},
		"spatial-association" : {
			paintStyle : dashedConnectorStyle,
			overlays : [[ "Label", { label:"", location:0.5, id:"description-label", cssClass: "description-label" } ],
			            [ "Label", { label:"", location:45, id:"cardinality-labelA", cssClass: "cardinality-label" } ],
			            [ "Label", { label:"", location:-45, id:"cardinality-labelB", cssClass: "cardinality-label" } ]],
			parameters: {
				"minA" : "",
				"maxA" : "",
				"minB" : "",
				"maxB" : "",
			},
		},
		"aggregation" : {
			paintStyle : defaultConnectorStyle,
			overlays : diamondOverlay,
		},
		"spatial-aggregation" : {
			paintStyle : dashedConnectorStyle,
			overlays : diamondOverlay,
		},
		"generalization-disjoint-partial" : {
			connector: ["Flowchart", {stub: [50, 30], alwaysRespectStubs: true}],
			paintStyle : defaultConnectorStyle,
			overlays : [ triangle("white", 13) ],
			parameters:{
				"participation":"partial",
				"disjointness":"disjoint",
			},
		},
		"generalization-overlapping-partial" : {
			connector: ["Flowchart", {stub: [50, 30], alwaysRespectStubs: true}],
			paintStyle : defaultConnectorStyle,
			overlays : [ triangle("black", 13) ],
			parameters:{
				"participation":"partial",
				"disjointness":"overlapping",
			},
		},
		"generalization-disjoint-total" : {
			connector: ["Flowchart", {stub: [50, 30], alwaysRespectStubs: true}],
			paintStyle : defaultConnectorStyle,
			overlays : [circle(9), triangle("white", 30) ],
			parameters:{
				"participation":"total",
				"disjointness":"disjoint",
			},
		},
		"generalization-overlapping-total" : {
			connector: ["Flowchart", {stub: [50, 30], alwaysRespectStubs: true}],
			paintStyle : defaultConnectorStyle,
			overlays : [circle(9), triangle("black", 30) ],
			parameters:{
				"participation":"total",
				"disjointness":"overlapping",
			},
		},
		"generalization-leg" : {
			connector: ["Flowchart", {stub: [50, 30], alwaysRespectStubs: true}],
			paintStyle : defaultConnectorStyle,
		},
		"arc-network" : {
			connector: "Straight",
			paintStyle : dashedConnectorStyle,
			overlays : [[ "Label", { label:"network", location:0.5, id:"description-label", cssClass: "arc-network-label" } ]],
		},
		"arc-network-sibling" : {
			connector: "Straight",
			paintStyle : dashedConnectorStyle,
		},		
		"arc-network-self" : {
			connector: ["Flowchart", {stub: [50, 50], alwaysRespectStubs: true}],
			paintStyle : dashedConnectorStyle,
			overlays : [[ "Label", { label:"network", location:0.4, id:"description-label", cssClass: "arc-network-label" } ]],
		},
		"arc-network-sibling-self" : {
			connector: ["Flowchart", {stub: [25, 25], alwaysRespectStubs: true}],
			paintStyle : dashedConnectorStyle,
		},
		"cartographic-generalization-disjoint" : {
			connector: ["Flowchart", {stub: [70, 50], alwaysRespectStubs: true}],
			paintStyle : dashedConnectorStyle,
			overlays : [square("white", 70), [ "Label", { label:"scale", location:0.5, id: "cartographic-label", cssClass: "cartographic-label" }]],
			parameters:{
				"disjointness":"disjoint"
			},
		},
		"cartographic-generalization-overlapping" : {
			connector: ["Flowchart", {stub: [70, 50], alwaysRespectStubs: true}],
			paintStyle : dashedConnectorStyle,
			overlays : [square("black", 70), [ "Label", { label:"scale", location:0.5, id: "cartographic-label", cssClass: "cartographic-label" }]],
			parameters:{
				"disjointness":"overlapping",
			},
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
		if(connection.source.classList[0] == "cartographic-square"){
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
					anchors : [ [ 0.35, 1, 0, 1 ], [ 1, 0.5, 1, 0 ] ],
					type : "arc-network-self",
					fireEvent: false,  // avoids this event loop
				});	

				var sibling = app.plumb.connect({
					source: info.connection.sourceId, 
					target: info.connection.targetId,
					anchors : [ [ 0.5, 1, 0, 1 ], [ 1, 0.75, 1, 0 ] ],
					type: "arc-network-sibling-self",
					parameters:{
						"sibling": newConn
					},
					fireEvent: false,
				});
				
				newConn.setParameter("sibling", sibling);
			}
			else{

				var sibling = app.plumb.connect({
					source:info.connection.sourceId, 
					target:info.connection.targetId,
					type:"arc-network-sibling",
					parameters:{
						"sibling": info.connection
					},
					fireEvent: false,
				});
				
				info.connection.setParameter("sibling", sibling);
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
			
			// Reatach the connection, but now to the endpoint
			var newConn = app.plumb.connect({
				anchors : [ "Bottom", "Top" ],
				source : endpoint,
				target : info.connection.targetId,
				type : type,
				fireEvent: false // avoids connect event loop
			});	
			
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
			return (sourceType == "conventional") && (targetType == "conventional");			

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

			if (confirm(app.deleteMsg)){
				app.plumb.detach(param);
			}
		}
		
		else if (type == "generalization-disjoint-partial"
			|| type == "generalization-disjoint-total"
				|| type == "generalization-overlapping-partial"
					|| type == "generalization-overlapping-total") {
			
			if (confirm(app.deleteMsg)){
				
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
			
			if (confirm(app.deleteMsg)){
				
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
