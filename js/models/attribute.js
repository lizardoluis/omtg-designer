'use strict';

var Attribute = Backbone.Model.extend({
	defaults : function() {
		return {
			name : '',
			type : '',
			defaultValue : '',
			isKey : false,
			length : '',
			scale : '',
			size : '',
			isNotNull : false,
			domain : ''
		}
	}
});