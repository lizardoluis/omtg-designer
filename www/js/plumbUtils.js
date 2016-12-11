'use strict';

app.plumbUtils = {

	NETWORK_DIST: 15, 	
		
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
    
    setOverlayTransformation : function(overlay, a, b, angle) {
    	overlay.style.webkitTransform = 'translate('+a+'px, '+b+'px) rotate('+angle+'rad)'; 
    	overlay.style.mozTransform    = 'translate('+a+'px, '+b+'px) rotate('+angle+'rad)'; 
    	overlay.style.msTransform     = 'translate('+a+'px, '+b+'px) rotate('+angle+'rad)'; 
    	overlay.style.oTransform      = 'translate('+a+'px, '+b+'px) rotate('+angle+'rad)'; 
    	overlay.style.transform       = 'translate('+a+'px, '+b+'px) rotate('+angle+'rad)';
    },
		
	updateLabelsPosition : function(connection){
		
		var type = connection.getType();
		
		if(type == "association" || type == "spatial-association"){
			var sourceEdge = connection.endpoints[0]._continuousAnchorEdge;
			var targetEdge = connection.endpoints[1]._continuousAnchorEdge;		
			this.setAssociationLabelClass(connection, "cardinality-labelA", sourceEdge);
			this.setAssociationLabelClass(connection, "cardinality-labelB", targetEdge);
			
			// Get elements
			var label = connection.getOverlay("description-label");
			var labelElement = label.getElement();
			var arrow = connection.getOverlay("description-arrow");
					
			// Get center position of diagrams
			var pSource = this.getPositionAtCenter(connection.source);
	        var pTarget = this.getPositionAtCenter(connection.target);
			
	        // Get orientation of the label segment
			var p = connection.connector.pointOnPath( label.loc );
			var segment = connection.connector.findSegmentForPoint(p.x, p.y);
	        
	        // When segment is horizontal
			if( (segment.y1 == segment.y2 && Math.abs(segment.x1 - segment.x2) > labelElement.offsetWidth) 
					|| (segment.y1 != segment.y2 && Math.abs(segment.y1 - segment.y2) <= arrow.getElement().width) ){				
				
				var angle = Math.PI;
				if(pSource.x <= pTarget.x){
					angle = 0;
				}
				
				this.setOverlayTransformation(labelElement, (-1)*labelElement.offsetWidth/2, (-1)*labelElement.offsetHeight/2-11 - Math.abs(segment.y1 - segment.y2)/2, 0);
				this.setOverlayTransformation(arrow.getElement(), (-1/2)*arrow.getElement().width, (-1)*labelElement.offsetHeight-9 - Math.abs(segment.y1 - segment.y2)/2, angle);
			}
			// When segment is vertical
			else{
				
				var angle = (-1)*Math.PI/2;				
				if(pSource.y <= pTarget.y){
					angle *= -1;
				}
				 
				this.setOverlayTransformation(labelElement, 22 + Math.abs(segment.x1 - segment.x2)/2, (-1)*labelElement.offsetHeight/2, 0);
				this.setOverlayTransformation(arrow.getElement(), -6 + Math.abs(segment.x1 - segment.x2)/2, (-1/2)*arrow.getElement().height, angle);			 
			}
			
			// Hide arrow if the description is empty. 
			// Occurs only on conventional association.
			if(label.getLabel() == ""){
				label.hide();
				arrow.hide();
			}
			else {
				label.show();
				arrow.show();
			}
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
				
				this.setOverlayTransformation(label, a, b, -1*rad);				
				
			} else if (x1 < x2 && y1 < y2) {
				
				var beta = Math.PI/2 - (-1)*rad;
				
				var a = 0 - label.offsetWidth/2 - Math.cos(beta)*dist/2;
				var b = 0 - label.offsetHeight/2 - Math.sin(beta)*dist/2;
				
				this.setOverlayTransformation(label, a, b, rad);	
				
			} else if (x1 > x2 && y1 < y2) {

				var beta = Math.PI/2 - rad;
				
				var a = 0 - label.offsetWidth/2 + Math.cos(beta)*dist/2;
				var b = 0 - label.offsetHeight/2 + Math.sin(beta)*dist/2;
				
				this.setOverlayTransformation(label, a, b, -1*rad);
				
			} else {

				var beta = Math.PI/2 - rad;
				
				var a = 0 - label.offsetWidth/2 - Math.cos(beta)*dist/2;
				var b = 0 - label.offsetHeight/2 + Math.sin(beta)*dist/2;
				
				this.setOverlayTransformation(label, a, b, rad);	
				
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
			
			var sx = this.NETWORK_DIST/sourceWidth;
			var sy = this.NETWORK_DIST/sourceHeight;
			var tx = this.NETWORK_DIST/targetWidth;
			var ty = this.NETWORK_DIST/targetHeight;				

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