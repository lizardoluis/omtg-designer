(function($) {
	'use strict';

	// Canvas View
	// ----------

	app.CanvasView = Backbone.View.extend({
		
		events : {
			'click' : 'clicked',
			'mousedown' : 'onMouseDown',
			'contextmenu' : 'openContextMenu'
		},

		initialize : function() {
			this.listenTo(this.model.get('diagrams'), 'add', this.addOMTGDiagram);
			this.listenTo(this.model.get('diagrams'), 'change', this.updateHistory);
			this.listenTo(this.model, 'change:activeTool', this.setCursor);
			this.listenTo(this.model, 'change:grid', this.toggleGrid);
			this.listenTo(this.model, 'change:diagramShadow', this.toggleDiagramShadow);
			this.listenTo(this.model, 'updateHistory', this.updateHistory);

			this._rubberBand = null;
			this._$selRect = null;
			this._didRubberBand = false;
		},
		
		clearCanvas : function() {
			app.plumb.detachEveryConnection({fireEvent : false});
			app.plumb.deleteEveryEndpoint();
			this.model.get('diagrams').removeAll();
		},

		updateHistory : function() {
			this.model.get('undoManager').update();
		},
		
		redoHistory : function() { 

			var undoManager = this.model.get('undoManager');
			
			if(undoManager.hasRedo()){
				this.clearCanvas(); 
				
				var xml = undoManager.redo();
				if(xml)
					app.XMLParser.parseOMTGSchema(xml);
			}
		},
		
		undoHistory : function() {

			var undoManager = this.model.get('undoManager');
			
			if(undoManager.hasUndo()){
				this.clearCanvas();  
				
				var xml = undoManager.undo();
				if(xml)
					app.XMLParser.parseOMTGSchema(xml);
			}
		},
		
		clicked : function(event) { 
		
			if (this._didRubberBand) {
				this._didRubberBand = false;
				return;
			}

			if (event && event.target && !$(event.target).is('.canvas')) 
				return;

			var tool = this.model.get('activeTool');

			if (tool) {				
				var grid = app.canvas.get("snapToGrid");				
				if (tool.get('model') == 'omtgDiagram') {
					var diagram = new app.omtg.Diagram({
						'type' : tool.get('name'),
						'left' : Math.round(event.offsetX / grid) * grid,
						'top' : Math.round(event.offsetY / grid) * grid
					});
					this.model.get('diagrams').add(diagram); 
					this.updateHistory(); 
				}
				tool.toggleActive();
				this.model.set('activeTool', null);
			}
			
			this.model.get('diagrams').unselectAll();
		},

		onMouseDown : function(event) {
			if (event.which !== 1) return;
			if (!$(event.target).is('.canvas')) return;
			if (this.model.get('activeTool')) return;

			var rect = this.$el[0].getBoundingClientRect();
			this._rubberBand = {
				startX: event.clientX - rect.left,
				startY: event.clientY - rect.top,
				active: false
			};

			var self = this;
			this._onDocMouseMove = function(e) { self._rubberBandMove(e); };
			this._onDocMouseUp = function(e) { self._rubberBandEnd(e); };
			$(document).on('mousemove', this._onDocMouseMove);
			$(document).on('mouseup', this._onDocMouseUp);

			event.preventDefault();
		},

		_rubberBandMove : function(event) {
			if (!this._rubberBand) return;

			var rect = this.$el[0].getBoundingClientRect();
			var curX = event.clientX - rect.left;
			var curY = event.clientY - rect.top;

			var dx = Math.abs(curX - this._rubberBand.startX);
			var dy = Math.abs(curY - this._rubberBand.startY);

			if (!this._rubberBand.active && (dx > 4 || dy > 4)) {
				this._rubberBand.active = true;
				this._$selRect = $('<div class="selection-rectangle"></div>');
				this.$el.append(this._$selRect);
			}

			if (this._rubberBand.active) {
				this._$selRect.css({
					left: Math.min(this._rubberBand.startX, curX) + 'px',
					top: Math.min(this._rubberBand.startY, curY) + 'px',
					width: dx + 'px',
					height: dy + 'px'
				});
			}
		},

		_rubberBandEnd : function(event) {
			$(document).off('mousemove', this._onDocMouseMove);
			$(document).off('mouseup', this._onDocMouseUp);

			if (!this._rubberBand) return;

			if (this._rubberBand.active) {
				this._didRubberBand = true;

				var rect = this.$el[0].getBoundingClientRect();
				var endX = event.clientX - rect.left;
				var endY = event.clientY - rect.top;

				var selLeft = Math.min(this._rubberBand.startX, endX);
				var selTop = Math.min(this._rubberBand.startY, endY);
				var selRight = Math.max(this._rubberBand.startX, endX);
				var selBottom = Math.max(this._rubberBand.startY, endY);

				var selected = [];
				this.model.get('diagrams').each(function(diagram) {
					var $el = $('#' + diagram.get('id'));
					if ($el.length === 0) return;
					var pos = $el.position();
					var dLeft = pos.left;
					var dTop = pos.top;
					var dRight = dLeft + $el.outerWidth();
					var dBottom = dTop + $el.outerHeight();

					if (dRight >= selLeft && dLeft <= selRight &&
						dBottom >= selTop && dTop <= selBottom) {
						selected.push(diagram);
					}
				});

				this.model.get('diagrams').selectMultiple(selected);

				if (this._$selRect) {
					this._$selRect.remove();
					this._$selRect = null;
				}
			}

			this._rubberBand = null;
		},

		setCursor : function() {			
			
			var tool = this.model.get('activeTool');

			if (tool && tool.get('model') == 'omtgDiagram') {
				this.$el.css("cursor", "copy");
			}
			else{
				this.$el.css("cursor", "default");
			}			
		},		
		
		toggleGrid : function() {		
			
			if(this.model.get('grid')){
				this.$el.addClass('canvas-background');
			}
			else{
				this.$el.removeClass('canvas-background');
			}
		},	
		
		toggleDiagramShadow : function() {	
			
			if(this.model.get('diagramShadow')){
				this.$el.find('.diagram-container').addClass('diagram-container-shadow');
			}
			else{
				this.$el.find('.diagram-container').removeClass('diagram-container-shadow');
			}
		},	
		
		addOMTGDiagram : function(diagram) {
			
			app.plumb.setSuspendDrawing(true);			
			
			var diagramView = new app.omtg.DiagramView({
				model : diagram
			});
			
			var dObject = diagramView.render().el;
			this.$el.append(dObject);
			
			//Plumbing			
			app.plumb.draggable(dObject, {
				containment : '#canvas',
				scroll : true,
				start: function() {
					app._wasDragging = false;
					var draggedId = dObject.id;
					var selected = app.canvas.get('diagrams').getSelected();
					var isInSelection = false;
					for (var i = 0; i < selected.length; i++) {
						if (selected[i].get('id') === draggedId) {
							isInSelection = true;
							break;
						}
					}
					if (isInSelection && selected.length > 1) {
						var $d = $(dObject);
						app._groupDrag = {
							draggedId: draggedId,
							startLeft: $d.position().left,
							startTop: $d.position().top,
							others: []
						};
						for (var i = 0; i < selected.length; i++) {
							var sid = selected[i].get('id');
							if (sid !== draggedId) {
								var $s = $('#' + sid);
								app._groupDrag.others.push({
									id: sid,
									startLeft: $s.position().left,
									startTop: $s.position().top
								});
							}
						}
					} else {
						app._groupDrag = null;
					}
				},
				drag: function() {
					app._wasDragging = true;
					if (app._groupDrag) {
						var $d = $(dObject);
						var dx = $d.position().left - app._groupDrag.startLeft;
						var dy = $d.position().top - app._groupDrag.startTop;
						for (var i = 0; i < app._groupDrag.others.length; i++) {
							var o = app._groupDrag.others[i];
							$('#' + o.id).css({
								left: (o.startLeft + dx) + 'px',
								top: (o.startTop + dy) + 'px'
							});
						}
					}
					app.plumb.repaintEverything();
				},
				stop: function() {
					if (app._groupDrag) {
						var grid = app.canvas.get("snapToGrid");
						var $d = $(dObject);
						var dx = $d.position().left - app._groupDrag.startLeft;
						var dy = $d.position().top - app._groupDrag.startTop;
						for (var i = 0; i < app._groupDrag.others.length; i++) {
							var o = app._groupDrag.others[i];
							var newLeft = Math.round((o.startLeft + dx) / grid) * grid;
							var newTop = Math.round((o.startTop + dy) / grid) * grid;
							var model = app.canvas.get('diagrams').findWhere({id: o.id});
							if (model) {
								model.set({left: newLeft, top: newTop});
							}
						}
						app.plumb.repaintEverything();
						app._groupDrag = null;
					}
				}
			});
			
			app.plumb.makeSource(dObject, {
				filter : function() {
					var tool = app.canvas.get('activeTool');
					return tool != null && tool.get('model') == 'omtgRelation';
				}
			});
			
			app.plumb.makeTarget(dObject);	
			app.plumb.setSuspendDrawing(false, true);
		},
		
		print : function(){
			this.model.get('diagrams').unselectAll();

			var $canvas = this.$el;
			var maxRight = 0;
			var maxBottom = 0;

			$canvas.find('.diagram-container').each(function(){
				var $d = $(this);
				var pos = $d.position();
				var right = pos.left + $d.outerWidth(true);
				var bottom = pos.top + $d.outerHeight(true);
				if(right > maxRight) maxRight = right;
				if(bottom > maxBottom) maxBottom = bottom;
			});

			var padding = 80;
			var origWidth = $canvas[0].style.width;
			var origHeight = $canvas[0].style.height;

			if(maxRight > 0 && maxBottom > 0){
				$canvas.css({
					'width': (maxRight + padding) + 'px',
					'height': (maxBottom + padding) + 'px'
				});
			}

			var restoreCanvas = function(){
				$canvas.css({
					'width': origWidth,
					'height': origHeight
				});
				window.removeEventListener('afterprint', restoreCanvas);
			};

			window.addEventListener('afterprint', restoreCanvas);
			window.print();
		},
		
		openContextMenu : function(event) { 
			
			if (event && event.target && !$(event.target).is('.canvas')) 
				return; 

			event.preventDefault();			
			
			app.contextMenuView = new app.ContextMenuView({left : event.pageX, top : event.pageY, offsetTop : event.offsetY, offsetLeft : event.offsetX});
		}
	});

})(jQuery);