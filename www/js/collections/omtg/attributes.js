(function() {
	'use strict';

	// OMTGDiagrams Collection
	// ----------
	
	app.omtg.Attributes = Backbone.Collection.extend({
		model : app.omtg.Attribute,
		
		toXML: function() {
			var xml = "";
			this.each(function(model) {
	    		xml += model.toXML();
	    	});
			return xml;
		},
	});
	
})();
	