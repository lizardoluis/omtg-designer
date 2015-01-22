(function() {
	'use strict';

	// OMTGDiagrams Collection
	// ----------
	
	app.omtg.Diagrams = Backbone.Collection.extend({
		model : app.omtg.Diagram,
		
		getType : function(id) {			
			var diagram = this.findWhere({id : id});
			if(diagram)
				return diagram.get('type');			
			return null;
		},
	});

})();
	