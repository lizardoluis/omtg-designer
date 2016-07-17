(function() {
	'use strict';

	// OMTGAttribute Model
	// ----------

	app.omtg.Attribute = Backbone.Model.extend({ 
		
		defaults : function() {
			return {
				name : '',
				type : 'INTEGER',
				defaultValue : '',
				isKey : false,
				length : '',
				scale : '',
				size : '',
				isNotNull : false,
				domain : '',
			};
		},
		
		// Return a copy of the model as XML
		toXML: function () {
			var xml = "<attribute>";
			
			xml += "<name>" + this.get('name') + "</name>";
			xml += "<type>" + this.get('type') + "</type>";
			
			if(this.get('isKey'))
				xml += "<key>" + this.get('isKey') + "</key>";

			if(this.get('length') != "")
				xml += "<length>" + this.get('length') + "</length>";

			if(this.get('scale') != "")
				xml += "<scale>" + this.get('scale') + "</scale>";
			
			if(this.get('isNotNull'))
				xml += "<not-null>" + this.get('isNotNull') + "</not-null>";
			
			if(this.get('defaultValue') != "")
				xml += "<default>" + this.get('defaultValue') + "</default>";

			if(this.get('size') != "")
				xml += "<size>" + this.get('size') + "</size>";		
			
			if(this.get('domain') != ""){
				xml += "<domain>";
				var values = this.domain.split("\n");		
				for ( var i = 0; i < values.length; i++) {
					xml += "<value>" + values[i] + "</value>";
				}		
				xml += "</domain>";
			}
			xml += "</attribute>";
			
			return xml;			
		},
	});

})();