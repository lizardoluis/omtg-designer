'use strict';

var Tool = Backbone.Model.extend({
	defaults : function() {
		return {
			name : '',
			type : '',
			tooltip: '',
			icon: ''
		}
	}
});