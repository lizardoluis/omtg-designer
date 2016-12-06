'use strict';

app.plumbUtils = {

	setAssociationLabelClass : function(connection, overlay, edge){
		
		var cardLabel = connection.getOverlay(overlay);
		
		cardLabel.removeClass("cardinality-left");
		cardLabel.removeClass("cardinality-right");
		cardLabel.removeClass("cardinality-top");
		cardLabel.removeClass("cardinality-bottom");
		
		switch(edge){
		case "left":
			cardLabel.addClass("cardinality-left");
			break;
		case "right":
			cardLabel.addClass("cardinality-right");
			break;
		case "top":
			cardLabel.addClass("cardinality-top");
			break;
		case "bottom":
			cardLabel.addClass("cardinality-bottom");
			break;
		default:
			cardLabel.addClass("cardinality-top");
			cardLabel.addClass("cardinality-left");
		}
	},
	
	calculateWordOffset : function(length, v0, v1){
		
		var min = 1;
		var max = 40;
				
		return (v1 - v0)*(length - min) / (max - min) + v0;
		
	},
	
	getPositionAtCenter : function (element) {
        var data = element.getBoundingClientRect();
        return {
            x: data.left + data.width / 2,
            y: data.top + data.height / 2
        };
    },

    getDistanceBetweenElements : function(a, b) {
        var aPosition = this.getPositionAtCenter(a);
        var bPosition = this.getPositionAtCenter(b);

        return Math.sqrt(
            Math.pow(aPosition.x - bPosition.x, 2) + 
            Math.pow(aPosition.y - bPosition.y, 2) 
        );
    },
    
    setLabelTransformation : function(label, a, b, angle) {
    	label.style.webkitTransform = 'translate('+a+'px, '+b+'px) rotate('+angle+'rad)'; 
		label.style.mozTransform    = 'translate('+a+'px, '+b+'px) rotate('+angle+'rad)'; 
		label.style.msTransform     = 'translate('+a+'px, '+b+'px) rotate('+angle+'rad)'; 
		label.style.oTransform      = 'translate('+a+'px, '+b+'px) rotate('+angle+'rad)'; 
		label.style.transform       = 'translate('+a+'px, '+b+'px) rotate('+angle+'rad)';
    },
		
	updateLabelsPosition : function(connection){
		
		var type = connection.getType();
		
		if(type == "association" || type == "spatial-association"){
			var sourceEdge = connection.endpoints[0]._continuousAnchorEdge;
			var targetEdge = connection.endpoints[1]._continuousAnchorEdge;		
			this.setAssociationLabelClass(connection, "cardinality-labelA", sourceEdge);
			this.setAssociationLabelClass(connection, "cardinality-labelB", targetEdge);
			
			var label = connection.getOverlay("description-label").getElement();
			this.setLabelTransformation(label, (-1)*label.offsetWidth/2, (-1)*label.offsetHeight/2-12, 0);
		}
		else if(type == "arc-network"){
			
			// Set anchors position
			this.repaintNetworkAnchors(connection);
			
			// Bounding box of the connection used to calculate the angle.
			var bBox = connection.getConnector().path.getBoundingClientRect();
			
			// Calculates the angle formed by the connection to rotate the label.			
			var rad = Math.asin((bBox.bottom - bBox.top) / Math.sqrt( (bBox.bottom - bBox.top) * (bBox.bottom - bBox.top) + (bBox.left - bBox.right) * (bBox.left - bBox.right) ));
					
			// Get distance between the connection and its sibling.
			var dist = this.getDistanceBetweenElements(connection.getConnector().path, connection.getParameter('sibling').getConnector().path);			
			
			// Get position of the endpoints to identify the quadrant formed by the angle of the connection and the elements.
			var x1 = parseInt(connection.endpoints[0].element.style.left.replace("px", ""));
			var y1 = parseInt(connection.endpoints[0].element.style.top.replace("px", ""));
			var x2 = parseInt(connection.endpoints[1].element.style.left.replace("px", ""));
			var y2 = parseInt(connection.endpoints[1].element.style.top.replace("px", ""));
				
			var label = connection.getOverlay("description-label").getElement();
			
			if (x1 < x2 && y1 > y2) {	
				
				var beta = Math.PI/2 - rad;
				
				var a = 0 - label.offsetWidth/2 - Math.cos(beta)*dist/2;
				var b = 0 - label.offsetHeight/2 - Math.sin(beta)*dist/2;
				
				this.setLabelTransformation(label, a, b, -1*rad);				
				
			} else if (x1 < x2 && y1 < y2) {
				
				var beta = Math.PI/2 - (-1)*rad;
				
				var a = 0 - label.offsetWidth/2 - Math.cos(beta)*dist/2;
				var b = 0 - label.offsetHeight/2 - Math.sin(beta)*dist/2;
				
				this.setLabelTransformation(label, a, b, rad);	
				
			} else if (x1 > x2 && y1 < y2) {

				var beta = Math.PI/2 - rad;
				
				var a = 0 - label.offsetWidth/2 + Math.cos(beta)*dist/2;
				var b = 0 - label.offsetHeight/2 + Math.sin(beta)*dist/2;
				
				this.setLabelTransformation(label, a, b, -1*rad);
				
			} else {

				var beta = Math.PI/2 - rad;
				
				var a = 0 - label.offsetWidth/2 - Math.cos(beta)*dist/2;
				var b = 0 - label.offsetHeight/2 + Math.sin(beta)*dist/2;
				
				this.setLabelTransformation(label, a, b, rad);	
				
			} 			
		}
	},
	
	repaintNetworkAnchors : function(connection){
		
		var type = connection.getType();
		
		if( type == "arc-network"){
			
			// get diagram size
			var sourceWidth = connection.source.getBoundingClientRect().width;
			var sourceHeight = connection.source.getBoundingClientRect().height;
			var targetWidth = connection.target.getBoundingClientRect().width;
			var targetHeight = connection.target.getBoundingClientRect().height;
			
			var sx = 15/sourceWidth;
			var sy = 15/sourceHeight;
			var tx = 15/targetWidth;
			var ty = 15/targetHeight;				

			var sibling = connection.getParameter("sibling");
			
			connection.endpoints[0].setAnchor([ [ 0.5 + sx, 0, 0, -1 ], [ 1, 0.5 + sy, 1, 0 ], [ 0.5 - sx, 1, 0, 1 ], [ 0, 0.5 - sy, -1, 0 ] ]);			
			connection.endpoints[1].setAnchor([ [ 0.5 - tx, 0, 0, -1 ], [ 1, 0.5 - ty, 1, 0 ], [ 0.5 + tx, 1, 0, 1 ], [ 0, 0.5 + ty, -1, 0 ] ]);			
			sibling.endpoints[0].setAnchor([ [ 0.5 - sx, 0, 0, -1 ], [ 1, 0.5 - sy, 1, 0 ], [ 0.5 + sx, 1, 0, 1 ], [ 0, 0.5 + sy, -1, 0 ] ]);
			sibling.endpoints[1].setAnchor([ [ 0.5 + tx, 0, 0, -1 ], [ 1, 0.5 + ty, 1, 0 ], [ 0.5 - tx, 1, 0, 1 ], [ 0, 0.5 - ty, -1, 0 ] ]);			
		}
	},
	
	repaintAllAnchors : function(){

		var connections = app.plumb.getConnections();
		
		for(var i=0; i<connections.length; i++){
			if(connections[i].getType() == "arc-network"){
				this.repaintNetworkAnchors(connections[i]);
			}
		}
	}
	
	
};