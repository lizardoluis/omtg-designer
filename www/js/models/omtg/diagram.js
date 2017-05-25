(function() {
	'use strict';
	
	// Diagram Model
	// ----------

	app.omtg.Diagram = Backbone.Model.extend({
		defaults : function() {
			return {
				id : this.cid, 
				type : '',
				name : 'Class_' + this.cid,
				attributes : new app.omtg.Attributes(),
				selected : false,
				top : 10,
				left : 10
			};
		},
		
		duplicate: function() {
			var offset = Math.floor(Math.random() * 31);
			
			var newDiagram = new app.omtg.Diagram({
				'type' : this.get('type'),
				'left' : this.get('left') + 40 + offset,
				'top' : this.get('top') + 40 + offset,
				'attributes' : this.get('attributes').clone()
			});
			
			for(var i=1; ; i++){
				var cloneName = this.get('name') + '_' + i;
				if( app.canvas.get('diagrams').findByName(cloneName) == null ){
					newDiagram.set('name', cloneName );
					break;
				}
			}
			
			return newDiagram;
		},
		
		// Move the diagram
		move: function(t, l) {
			this.set('top', this.get('top') + t);
			this.set('left', this.get('left') + l);
		},
		
		// Toggle the `selected` state of this diagram.
		toggleSelected: function () {
			this.set('selected', !this.get('selected'));
		},
		
		// Return a copy of the model as XML
		toXML: function () {
			return "<class>" +
			"<name>" + this.get('name') + "</name>" +
			"<top>" + this.get('top') + "</top>" +
			"<left>" + this.get('left') + "</left>" +
			"<type>" + this.get('type') + "</type>" +
			"<attributes>" + this.get('attributes').toXML() + "</attributes>" +
			"</class>";			
		},
	});
	
})();