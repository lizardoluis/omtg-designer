'use strict';

var Toolbox = Backbone.Model.extend({
	defaults : function() {
		return {
			name : '',
			tools : null
		}
	}
});