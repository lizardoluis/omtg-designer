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
		
	updateLabelsPosition : function(connection){
		
		var type = connection.getType();
		
		if(type == "association" || type == "spatial-association"){
			var sourceEdge = connection.endpoints[0]._continuousAnchorEdge;
			var targetEdge = connection.endpoints[1]._continuousAnchorEdge;		
			this.setAssociationLabelClass(connection, "cardinality-labelA", sourceEdge);
			this.setAssociationLabelClass(connection, "cardinality-labelB", targetEdge);
		}
		else if(type == "arc-network"){
			
			// Get position of the endpoint to calculate the quadrants
			var x1 = parseInt(connection.endpoints[0].element.style.left.replace("px", ""));
			var y1 = parseInt(connection.endpoints[0].element.style.top.replace("px", ""));
			var x2 = parseInt(connection.endpoints[1].element.style.left.replace("px", ""));
			var y2 = parseInt(connection.endpoints[1].element.style.top.replace("px", ""));
			
			// Bounding box of the connection used to calculate the angle.
			var bBox = connection.getConnector().path.getBoundingClientRect();
			
			var sin = (bBox.top - bBox.bottom) / Math.sqrt( (bBox.top - bBox.bottom) * (bBox.top - bBox.bottom) + (bBox.right - bBox.left) * (bBox.right - bBox.left) );			
			
			// Calculates the rotation in rad degrees.
			var rad = Math.asin(sin);
			
			// Calculates the translation for x and y axis.
			var a0, a1, b0, b1, r0, r1;
			
			if (x1 < x2 && y1 > y2) {				
				a0 = (-50);
				a1 = (-85);				
				b0 = (-110);
				b1 = (-50);				
				r0 = 0;
				r1 = (-1) * Math.PI/2;
			} else if (x1 < x2 && y1 < y2) {
				rad *= -1;				
				a0 = (-50);
				a1 = (-10);				
				b0 = (-110);
				b1 = (-50);				
				r0 = 0;
				r1 = Math.PI/2;
			} else if (x1 > x2 && y1 < y2) {
				a0 = (-10);
				a1 = (-50);				
				b0 = (-50);
				b1 = (0);				
				r0 = (-1) * Math.PI/2;
				r1 = 0;
			} else {
				rad *= -1;				
				a0 = (-50);
				a1 = (-85);				
				b0 = (0);
				b1 = (-50);				
				r0 = 0;
				r1 = Math.PI/2;
			}

			var a = (a1 - a0)*(rad - r0) / (r1 - r0) + a0;
			var b = (b1 - b0)*(rad - r0) / (r1 - r0) + b0;
			
			var labelStyle = connection.getOverlay("description-label").getElement().style;
			
			labelStyle.webkitTransform = 'translate('+a+'%, '+b+'%) rotate('+rad+'rad)'; 
			labelStyle.mozTransform    = 'translate('+a+'%, '+b+'%) rotate('+rad+'rad)'; 
			labelStyle.msTransform     = 'translate('+a+'%, '+b+'%) rotate('+rad+'rad)'; 
			labelStyle.oTransform      = 'translate('+a+'%, '+b+'%) rotate('+rad+'rad)'; 
			labelStyle.transform       = 'translate('+a+'%, '+b+'%) rotate('+rad+'rad)';
		}
	}
};