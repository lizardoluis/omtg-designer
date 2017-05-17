(function() {
	'use strict';

	// Tool Model
	// ----------

	app.UndoManager = Backbone.Model.extend({
		defaults : function() {
			return {
				historyIndex : -1,
				history : [],
			};
		},
		
		update: function() { 
			console.log("update"); 
			
			var xml = app.canvas.toXML();
			
			var history = _.clone(this.get('history'));
			var index = this.get('historyIndex');
			
			if(!history || history[index] != xml){	
				
				history = history.slice(0, index + 1);				
				history.push(xml);
				
				this.set('history', history);
				this.set('historyIndex', ++index);
			}			
		},
		
		redo: function () { 
			
			console.log("redo");
			
		    var index = this.get('historyIndex');
			
			if (index < this.get('history').length){ 
				this.set('historyIndex', ++index);			
				return this.get('history')[index];
			}
			
			return "";
		},
		
		undo: function () {
			
			console.log("undo");
			
			var index = this.get('historyIndex');
			
			if (index >= 0){ 
				this.set('historyIndex', --index);				
				return this.get('history')[index];
			}
			
			return "";
		},
		
		hasRedo: function() {
			var index = this.get('historyIndex');			
			if(index !== this.get('history').length - 1){
				return true;
			}
			return false;
		},
		
		hasUndo: function() {
			var index = this.get('historyIndex');
			if(index >= 0){
				return true;
			}
			return false;
		}
	});

})();