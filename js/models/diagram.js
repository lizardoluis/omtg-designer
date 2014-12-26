'use strict';

var Diagram = Backbone.Model.extend({
	defaults : function() {
		return {
			id : this.cid,
			type : '',
			name : '',
			attributes: new Attributes()
		}
	}
});