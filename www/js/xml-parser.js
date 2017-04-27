'use strict';

app.XMLParser = {

	parseOMTGSchema : function(xml) {

		// Suspend jsPlumb Drawing
		app.plumb.setSuspendDrawing(true);	

		var domParser = new DOMParser();
		var xmlDoc = domParser.parseFromString (xml, "text/xml");

		// Maps diagrams names into diagrams ids
		this.diagramMap = {};

		// Gets from XML the diagrams and connections
		var diagrams = xmlDoc.getElementsByTagName("classes")[0].childNodes;
		var connections = xmlDoc.getElementsByTagName("relationships")[0].childNodes;				

		// Parse and adds to the canvas the diagrams and connections
		this.parseOMTGDiagrams(diagrams);
		this.parseOMTGConnections(connections);

		// Enable jsPlumb Drawing
		app.plumb.setSuspendDrawing(false, true);	
		
		// Adjust labels
		app.plumbUtils.repaintAllAnchors();
		app.plumbUtils.repaintAllLabels();
	},
	
	parseOMTGDiagrams : function(elements) {
		for ( var i = 0; i < elements.length; i++) {
			var node = elements.item(i);
			var diagram = this.parseOMTGDiagram(node);
			
			app.canvas.get('diagrams').add(diagram);
		}	
	},
	
	parseOMTGDiagram : function(element) {
		var children = element.childNodes;
		var diagram = new app.omtg.Diagram();

		for ( var i = 0; i < children.length; i++) {
			switch (children.item(i).nodeName) {
			case "name":
				diagram.set('name', children.item(i).firstChild.nodeValue);
				break;
			case "top":
				diagram.set('top', parseFloat(children.item(i).firstChild.nodeValue));
				break;
			case "left":
				diagram.set('left', parseFloat(children.item(i).firstChild.nodeValue));
				break;
			case "type":
				diagram.set('type', children.item(i).firstChild.nodeValue);
				break;
			case "attributes":
				diagram.set('attributes', this.parseOMTGAttributes(children.item(i)));
				break;
			};
		}
		
		this.diagramMap[diagram.get('name')] = diagram.get('id');
		
		return diagram;
	},
	
	parseOMTGAttributes : function(element) {
		var children = element.childNodes;	
		var attributes = new app.omtg.Attributes();
		
		for ( var i = 0; i < children.length; i++) {
			var attribute = this.parseOMTGAttribute(children.item(i));
			attributes.add(attribute);
		}
		
		return attributes;
	},
	
	parseOMTGAttribute : function(element) {
		var children = element.childNodes;
		var attribute = new app.omtg.Attribute();
		
		for ( var i = 0; i < children.length; i++) {	
			switch(children.item(i).nodeName){
			case "id":
				attribute.set('id', children.item(i).firstChild.nodeValue);
				break;
			case "name":
				attribute.set('name', children.item(i).firstChild.nodeValue);
				break;
			case "type":
				attribute.set('type', children.item(i).firstChild.nodeValue);
				break;
			case "default":
				attribute.set('defaultValue', children.item(i).firstChild.nodeValue);
				break;
			case "key":
				attribute.set('isKey', children.item(i).firstChild.nodeValue);
				break;
			case "length":
				attribute.set('length', children.item(i).firstChild.nodeValue);
				break;
			case "scale":
				attribute.set('scale', children.item(i).firstChild.nodeValue);
				break;
			case "size":
				attribute.set('size', children.item(i).firstChild.nodeValue);
				break;
			case "not-null":
				attribute.set('isNotNull', children.item(i).firstChild.nodeValue);
				break;
			case "domain":
				attribute.set('domain', this.parseOMTGAttributeDomain(children.item(i)));
				break;
			}
		}	

		return attribute;
	},
	
	parseOMTGAttributeDomain : function(element){
		//TODO: implement domain
		return null;
	},
	
	parseOMTGConnections : function(elements) {
		for ( var i = 0; i < elements.length; i++) {
			var node = elements.item(i);
			this.parseOMTGConnection(node);
		}	
	},
	
	parseOMTGConnection : function(element) {
		
		switch(element.nodeName){
		case "conventional":
			var description = this.getValue(element.childNodes[0].firstChild);
			var sourceName = element.childNodes[1].firstChild.nodeValue;
			var targetName = element.childNodes[3].firstChild.nodeValue;
			var cardinalityA = element.childNodes[2];
			var cardinalityB = element.childNodes[4];
			
			var connection = app.plumb.connect({
				source: this.diagramMap[sourceName],
				target: this.diagramMap[targetName],
				fireEvent: false
			});
			connection.setType('association');
						
			connection.getOverlay("description-label").setLabel(description);
			
			this.parseOMTGConnectionCardinality(cardinalityA, connection, 'A');
			this.parseOMTGConnectionCardinality(cardinalityB, connection, 'B');
			
			break;
			
		case "topological":			
			var spatialRelation = this.getValue(element.childNodes[0].firstChild.firstChild);
			var sourceName = element.childNodes[1].firstChild.nodeValue;
			var targetName = element.childNodes[3].firstChild.nodeValue;
			var cardinalityA = element.childNodes[2];
			var cardinalityB = element.childNodes[4];
			
			var connection = app.plumb.connect({
				source: this.diagramMap[sourceName],
				target: this.diagramMap[targetName],
				fireEvent: false
			});
			connection.setType('spatial-association');
			
			if(spatialRelation.toLowerCase() == 'near' || spatialRelation.toLowerCase() == 'distant'){
				var distance = this.getValue(element.childNodes[0].childNodes[1].firstChild);
				connection.setParameter("distance", distance);
				connection.getOverlay("description-label").setLabel(spatialRelation + ' (' + distance + ')');
			}
			else{
				connection.getOverlay("description-label").setLabel(spatialRelation);
			}
			
			this.parseOMTGConnectionCardinality(cardinalityA, connection, 'A');
			this.parseOMTGConnectionCardinality(cardinalityB, connection, 'B');
			
			break;
			
		case "conventional-aggregation":
			var sourceName = element.childNodes[0].firstChild.nodeValue;
			var targetName = element.childNodes[1].firstChild.nodeValue;
			
			var connection = app.plumb.connect({
				source: this.diagramMap[sourceName],
				target: this.diagramMap[targetName],
				fireEvent: false
			});			
			connection.setType('aggregation');
			
			break;
			
		case "spatial-aggregation":
			var sourceName = element.childNodes[0].firstChild.nodeValue;
			var targetName = element.childNodes[1].firstChild.nodeValue;
			
			var connection = app.plumb.connect({
				source: this.diagramMap[sourceName],
				target: this.diagramMap[targetName],
				fireEvent: false
			});		
			connection.setType('spatial-aggregation');
			
			break;
			
		case "network":
			var description = this.getValue(element.childNodes[0].firstChild);
			var sourceName = element.childNodes[1].firstChild.nodeValue;
			var targetName = element.childNodes[2].firstChild.nodeValue;
			var type = "arc-network";
			var typeSibling = "arc-network-sibling";	
			var anchors = ["Continuous", "Continuous"];
			var anchorsSibling = ["Continuous", "Continuous"];
			var connnectionConnector = "Straight";
			var siblingConnector = "Straight";
			
			if(sourceName == targetName){
				type = "arc-network-self";
				typeSibling = "arc-network-sibling-self";
				anchors = [ [ 0.35, 1, 0, 1 ], [ 1, 0.5, 1, 0 ] ];
				anchorsSibling = [ [ 0.5, 1, 0, 1 ], [ 1, 0.75, 1, 0 ] ];
				connnectionConnector = ["Flowchart", {stub: [50, 50], alwaysRespectStubs: true}];
				siblingConnector = ["Flowchart", {stub: [25, 25], alwaysRespectStubs: true}];
			}
			
			var connection = app.plumb.connect({
				source : this.diagramMap[sourceName],
				target : this.diagramMap[targetName],
				anchors : anchors,
				fireEvent : false
			});
			connection.setConnector(connnectionConnector);
			connection.setType(type);
			connection.getOverlay("description-label").setLabel(description);			
			
			var sibling = app.plumb.connect({
				source : this.diagramMap[sourceName],
				target : this.diagramMap[targetName],
				anchors : anchorsSibling,
				fireEvent : false
			});
			sibling.setConnector(siblingConnector);	
			sibling.setType(typeSibling);
			
			// set parameters			
			connection.setParameter("sibling", sibling);
			sibling.setParameter("sibling", connection);
			
			break;
			
		case "conceptual-generalization":
			var sourceName = element.childNodes[0].firstChild.nodeValue;
			var disjointness = element.childNodes[2].firstChild.nodeValue;
			var targetName = element.childNodes[3].childNodes[0].firstChild.nodeValue;
			var subDiagrams = element.childNodes[3].childNodes;
						
			var connection = app.plumb.connect({
				source: this.diagramMap[sourceName],
				target: this.diagramMap[targetName],
				anchors : [ "Bottom", "Top" ],
				fireEvent: false
			});
			connection.setConnector(["Flowchart", {stub: [70, 50], alwaysRespectStubs: true}]);
			connection.setType('cartographic-generalization-' + disjointness);
			
			// Make the middle square of the connection a source of connections
			var square = connection.getOverlay("cartographic-square").getElement();
			app.plumb.makeSource(square, {
				anchor:[ "Right", "Left" ]
			});
			
			for(var i=1; i<subDiagrams.length; i++){
				var c = app.plumb.connect({
					source: square,
					target: this.diagramMap[subDiagrams[i].firstChild.nodeValue],
					fireEvent: false
				});
				c.endpoints[1].setAnchor("Top");
				c.setConnector(["Flowchart", {stub: [0, 50], alwaysRespectStubs: true}]);
				c.setType("cartographic-leg");
			}
			
			break;
			
		case "generalization":
			var sourceName = element.childNodes[0].firstChild.nodeValue;
			var participation = element.childNodes[1].firstChild.nodeValue;
			var disjointness = element.childNodes[2].firstChild.nodeValue;
			var subDiagrams = element.childNodes[3].childNodes;
			var type = "generalization-" + disjointness + "-" + participation;
			
			// Duplicated function of the plumb.js.
			// TODO: crate a global function
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
			
			// Duplicated function of the plumb.js.
			// TODO: crate a global function
			var generalizationEndpointAnchor = function(type){
				if(type == "total")
					return [ 0.5, 1, 0, 1, 0, 20 ];
				else
					return [ 0.5, 1, 0, 1, 0, 11 ];
			}
			
			var endpoint = app.plumb.addEndpoint(this.diagramMap[sourceName], {
				anchor : generalizationEndpointAnchor(participation), 
				connectionType : "generalization-leg",
				endpoint : triangleEndpoint(type),
				isSource : true,
				isTarget : false,
				maxConnections : -1,
				uniqueEndpoint : true,				
				parameters:{
					"participation": participation,
					"disjointness": disjointness,
			    }
			});
			
			for(var i=0; i<subDiagrams.length; i++){
				var c = app.plumb.connect({
					source: endpoint,
					anchors : [ "Bottom", "Top" ],
					target: this.diagramMap[subDiagrams[i].firstChild.nodeValue],
					fireEvent : false
				});
				if(i==0){
					c.setType(type);
				}
				else{
					c.setType("generalization-leg");
				}
				c.setConnector(["Flowchart", {stub: [50, 30], alwaysRespectStubs: true}]);
			}
			
			break;
		}
	},
	
	parseOMTGConnectionCardinality : function(element, connection, side){
		
		if(side != 'A' && side != 'B')
			return;
		
		var min = "", max = "", str = "";
		
		min = this.getValue(element.childNodes[0].firstChild);		
		max = this.getValue(element.childNodes[1].firstChild);
		
		// This code is equal to connection editor view. 
		// It will be better to create a model for connection and the view to process the model.
		min = min >= 0 && min != ""  ? min : '0';
		max = max >= 1 && max != "" && min <= max ? max : '*';
		
		if(min == max){
			str = min;
		}
		else{
			str = min + ".." + max;
		}

		connection.getOverlay("cardinality-label" + side).setLabel(str);	
		
		connection.setParameter("min" + side, min);
		connection.setParameter("max" + side, max);
	},
	
	getValue : function(p) {
		if (p)
			return p.nodeValue;
		else
			return "";
	},
};