'use strict';

app.XMLParser = {

	parseOMTGSchema : function(xml) {
		
		var domParser = new DOMParser();
		
		var xmlDoc = domParser.parseFromString (xml, "text/xml");
		
		this.diagramMap = {};

		var diagrams = xmlDoc.getElementsByTagName("classes")[0].childNodes;
		var connections = xmlDoc.getElementsByTagName("relationships")[0].childNodes;
		
		this.parseOMTGDiagrams(diagrams);
		this.parseOMTGConnections(connections);
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
				type: 'association',
			});
						
			connection.getOverlay("description-label").setLabel(description);
			
			this.parseOMTGConnectionCardinality(cardinalityA, connection, 'A');
			this.parseOMTGConnectionCardinality(cardinalityB, connection, 'B');
			
			break;
			
		case "topological":			
			var description = this.getValue(element.childNodes[0].firstChild.firstChild);
			var sourceName = element.childNodes[1].firstChild.nodeValue;
			var targetName = element.childNodes[3].firstChild.nodeValue;
			var cardinalityA = element.childNodes[2];
			var cardinalityB = element.childNodes[4];
			
			var connection = app.plumb.connect({
				source: this.diagramMap[sourceName],
				target: this.diagramMap[targetName],
				type: 'spatial-association',
			});
						
			connection.getOverlay("description-label").setLabel(description);
			
			this.parseOMTGConnectionCardinality(cardinalityA, connection, 'A');
			this.parseOMTGConnectionCardinality(cardinalityB, connection, 'B');
			
			break;
			
		case "conventional-aggregation":
			var sourceName = element.childNodes[0].firstChild.nodeValue;
			var targetName = element.childNodes[1].firstChild.nodeValue;
			
			app.plumb.connect({
				source: this.diagramMap[sourceName],
				target: this.diagramMap[targetName],
				type: 'aggregation',
			});			
			
			break;
			
		case "spatial-aggregation":
			var sourceName = element.childNodes[0].firstChild.nodeValue;
			var targetName = element.childNodes[1].firstChild.nodeValue;
			
			app.plumb.connect({
				source: this.diagramMap[sourceName],
				target: this.diagramMap[targetName],
				type: 'spatial-aggregation',
			});			
			
			break;
			
		case "network":
			var description = this.getValue(element.childNodes[0].firstChild);
			var sourceName = element.childNodes[1].firstChild.nodeValue;
			var targetName = element.childNodes[2].firstChild.nodeValue;
			
			var connection = app.plumb.connect({
				source: this.diagramMap[sourceName],
				target: this.diagramMap[targetName],
				type: 'arc-network',
			});
			
			connection.getOverlay("description-label").setLabel(description);
			
			break;
			
		case "conceptual-generalization":
			var sourceName = element.item(i).childNodes[0].firstChild.nodeValue;
			var type = element.item(i).childNodes[1].firstChild.nodeValue;
			var disjointness = element.item(i).childNodes[2].firstChild.nodeValue;
			var targetName = element.item(i).childNodes[3].childNodes[0].firstChild.nodeValue;
			var subDiagrams = element.item(i).childNodes[3].childNodes;
			
			var connection = app.plumb.connect({
				source: this.diagramMap[sourceName],
				target: this.diagramMap[targetName],
				type: 'cartographic-generalization-' + disjointness,
			});
			
			break;
		}
	},
	
	parseOMTGConnectionCardinality : function(element, connection, side){
		
		if(side != 'A' && side != 'B')
			return;
		
		var min = "", max = "", str = "";
		
		min = this.getValue(element.childNodes[0].firstChild);		
		max = this.getValue(element.childNodes[1].firstChild);
		
		if(min == "" && max == ""){
			str = "";
		}
		else if (min != "" && max != ""){
			str =  min + ".." + max;
		}
		else {
			str = min + "" + max;
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