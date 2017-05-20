(function() {
	'use strict';

	// Tool Model
	// ----------

	app.Canvas = Backbone.Model.extend({
		defaults : function() {
			return {
				diagrams : new app.omtg.Diagrams(),
				activeTool : null,
				grid : true,
				diagramShadow : true,
				snapToGrid : 10,
				clipboard : null,
				undoManager : new app.UndoManager(),
			}; 
		},
		
		toXML : function() {
						
			var xml = '<?xml version="1.0" encoding="UTF-8"?>'
				+ '<omtg-conceptual-schema xsi:noNamespaceSchemaLocation="omtg-schema-template.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">'
				+ this.get('diagrams').toXML()
				+ this.connectionsToXML()
				+ '</omtg-conceptual-schema>';

			return xml;
		},
		
		// Convert all connections to XML
		connectionsToXML : function() {
			var conns = app.plumb.getConnections();
			var connsXML = "";
						
			for(var i=0; i<conns.length; i++){
				var type = conns[i].getType()[0];
				if (type != "arc-network-sibling"
					&& type != "cartographic-leg" 
						&& type != "generalization-leg"){
					connsXML += this.connectionToXML(conns[i]);
				}
	    	}
			
			return "<relationships>" + connsXML + "</relationships>";			
		},		
		
		// Convert a connection to XML
		connectionToXML : function(conn){
			
			var type = conn.getType()[0];
			
			switch(type){
			case "aggregation":
				var sourceName = this.get('diagrams').getAttrById(conn.sourceId, 'name');
				var targetName = this.get('diagrams').getAttrById(conn.targetId, 'name');
				return "<conventional-aggregation>" +
				"<class1>" + sourceName + "</class1>" +
				"<class2>" + targetName + "</class2>" +
				"</conventional-aggregation>";
				
			case "spatial-aggregation":
				var sourceName = this.get('diagrams').getAttrById(conn.sourceId, 'name');
				var targetName = this.get('diagrams').getAttrById(conn.targetId, 'name');
				return "<spatial-aggregation>" +
				"<class1>" + sourceName + "</class1>" +
				"<class2>" + targetName + "</class2>" +
				"</spatial-aggregation>";
				
			case "association":
				var description = conn.getOverlay("description-label").getLabel();
				var sourceName = this.get('diagrams').getAttrById(conn.sourceId, 'name');
				var targetName = this.get('diagrams').getAttrById(conn.targetId, 'name');
				var minA = conn.getParameter("minA");
				var maxA = conn.getParameter("maxA");
				var minB = conn.getParameter("minB");
				var maxB = conn.getParameter("maxB");			
				return "<conventional>" +
				"<name>" + description + "</name>" +
				"<class1>" + sourceName + "</class1><cardinality1><min>" + minA + "</min><max>" + maxA + "</max></cardinality1>" +
				"<class2>" + targetName + "</class2><cardinality2><min>" + minB + "</min><max>" + maxB + "</max></cardinality2>" +
				"</conventional>";
				
			case "spatial-association":
				var description = conn.getOverlay("description-label").getLabel().split(" ")[0];
				var sourceName = this.get('diagrams').getAttrById(conn.sourceId, 'name');
				var targetName = this.get('diagrams').getAttrById(conn.targetId, 'name');
				var minA = conn.getParameter("minA");
				var maxA = conn.getParameter("maxA");
				var minB = conn.getParameter("minB");
				var maxB = conn.getParameter("maxB");	
				var distance = conn.getParameter("distance");	
				return "<topological>" +
				"<spatial-relations><spatial-relation>" + description + "</spatial-relation><distance>" + distance + "</distance></spatial-relations>" +
				"<class1>" + sourceName + "</class1><cardinality1><min>" + minA + "</min><max>" + maxA + "</max></cardinality1>" +
				"<class2>" + targetName + "</class2><cardinality2><min>" + minB + "</min><max>" + maxB + "</max></cardinality2>" +
				"</topological>";
				
			case "arc-network":
			case "arc-network-self":
				var description = conn.getOverlay("description-label").getLabel();
				var sourceName = this.get('diagrams').getAttrById(conn.sourceId, 'name');
				var targetName = this.get('diagrams').getAttrById(conn.targetId, 'name');
				return "<network>" +
				"<name>" + description + "</name>" +
				"<class1>" + sourceName + "</class1>" +
				"<class2>" + targetName + "</class2>" +
				"</network>";
				
			case "generalization-disjoint-partial":
			case "generalization-disjoint-total":
			case "generalization-overlapping-partial":
			case "generalization-overlapping-total":				
				var superName = this.get('diagrams').getAttrById(conn.sourceId, 'name');
				
				var endpoint = conn.endpoints[0];				
				var participation = endpoint.getParameter("participation");
				var disjointness = endpoint.getParameter("disjointness");

				var subClasses = "";
				var subConns = endpoint.getAttachedElements();
				for(var i=0; i<subConns.length; i++){
					var subName = this.get('diagrams').getAttrById(subConns[i].targetId, 'name');
		    		subClasses += "<subclass>" + subName + "</subclass>";
		    	}
				
				return "<generalization>" +
				"<superclass>" + superName + "</superclass>" +
				"<participation>" + participation + "</participation>" +
				"<disjointness>" + disjointness + "</disjointness>" +
				"<subclasses>" + subClasses + "</subclasses>" +
				"</generalization>";
				
			case "cartographic-generalization-disjoint":
			case "cartographic-generalization-overlapping":
				var superName = this.get('diagrams').getAttrById(conn.sourceId, 'name');
				var disjointness = conn.getParameter("disjointness");
				var description = conn.getOverlay("cartographic-label").getLabel();
				
				var subClasses = "<subclass>" + this.get('diagrams').getAttrById(conn.targetId, 'name') + "</subclass>";
				
				var subConns = app.plumb.getConnections({source : conn.getOverlay("cartographic-square").getElement()});
				for(var i=0; i<subConns.length; i++) {
					var subName = this.get('diagrams').getAttrById(subConns[i].targetId, 'name');
		    		subClasses += "<subclass>" + subName + "</subclass>";
		    	}
				
				return "<conceptual-generalization>" +
				"<superclass>" + superName + "</superclass>" +
				"<scale-shape>" + description + "</scale-shape>" + 
				"<disjointness>" + disjointness + "</disjointness>" +
				"<subclasses>" + subClasses + "</subclasses>" +
				"</conceptual-generalization>";
				
			default:
				return "";
			}
		}
	});

})();