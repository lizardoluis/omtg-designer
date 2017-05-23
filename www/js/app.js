// Namespaces

var ENTER_KEY = 13;
var ESC_KEY = 27;
var LEFT_ARROW_KEY = 37;
var TOP_ARROW_KEY = 38;
var RIGHT_ARROW_KEY = 39;
var DOWN_ARROW_KEY = 40;
var Z_KEY = 90;

var app = {
	// OMT-G namespace
	omtg : {},

	msgs : {
		DELETE_CONNECTION: "This connection will be detached. There is no undo. Are you sure?",
		DELETE_DIAGRAM: "This diagram will be removed. All its connection will be detached. There is no undo. Are you sure?",
		EMPTY_PROJECT: "Project is empty, there is nothing to export!",
		NOT_EMPTY_PROJECT: "Project is not empty",
	},
};

$(function () {
	'use strict';
	
	// Diagrams
	app.classTools = new app.Tools([
	     { name : 'polygon', model : 'omtgDiagram', tooltip: 'Polygon', icon: 'imgs/omtg/polygon.png' },
	     { name : 'line', model : 'omtgDiagram', tooltip: 'Line', icon: 'imgs/omtg/line.png' },	
	     { name : 'point', model : 'omtgDiagram', tooltip: 'Point', icon: 'imgs/omtg/point.png' },
	     { name : 'node', model : 'omtgDiagram', tooltip: 'Node', icon: 'imgs/omtg/node.png' },
	     { name : 'isolines', model : 'omtgDiagram', tooltip: 'Isolines', icon: 'imgs/omtg/isolines.png' },
	     { name : 'planar-subdivision', model : 'omtgDiagram', tooltip: 'Planar Subdivision', icon: 'imgs/omtg/planar-subdivision.png' },
	     { name : 'TIN', model : 'omtgDiagram', tooltip: 'Triangular Irregular Network', icon: 'imgs/omtg/TIN.png' },
	     { name : 'tesselation', model : 'omtgDiagram', tooltip: 'Tesselation', icon: 'imgs/omtg/tesselation.png' },
	     { name : 'sample', model : 'omtgDiagram', tooltip: 'Sample', icon: 'imgs/omtg/sample.png' },
	     { name : 'un-line', model : 'omtgDiagram', tooltip: 'Unidirectional Line', icon: 'imgs/omtg/un-line.png' },
	     { name : 'bi-line', model : 'omtgDiagram', tooltip: 'Bidirectional Line', icon: 'imgs/omtg/bi-line.png' },
	     { name : 'conventional', model : 'omtgDiagram', tooltip: 'Conventional', icon: 'imgs/omtg/conventional.png' }
	]);

	
	// Relations
	app.relationshipTools = new app.Tools([
	     { name : 'association', model : 'omtgRelation', tooltip: 'Association', icon: 'imgs/relation/association.png' },
	     { name : 'spatial-association', model : 'omtgRelation', tooltip: 'Spatial Association', icon: 'imgs/relation/spatial-association.png' },
	     { name : 'aggregation', model : 'omtgRelation', tooltip: 'Aggregation', icon: 'imgs/relation/aggregation.png' },
	     { name : 'spatial-aggregation', model : 'omtgRelation', tooltip: 'Spatial Aggregation', icon: 'imgs/relation/spatial-aggregation.png' },
	     { name : 'cartographic-generalization-overlapping', model : 'omtgRelation', tooltip: 'Cartographic Generalization Overlapping', icon: 'imgs/relation/cartographic-generalization-overlapping.png' },
	     { name : 'cartographic-generalization-disjoint', model : 'omtgRelation', tooltip: 'Cartographic Generalization Disjoint', icon: 'imgs/relation/cartographic-generalization-disjoint.png' },
	     { name : 'generalization-disjoint-partial', model : 'omtgRelation', tooltip: 'Generalization Disjoint-Partial', icon: 'imgs/relation/generalization-disjoint-partial.png' },
	     { name : 'generalization-overlapping-partial', model : 'omtgRelation', tooltip: 'Generalization Overlapping-Partial', icon: 'imgs/relation/generalization-overlapping-partial.png' },
	     { name : 'generalization-disjoint-total', model : 'omtgRelation', tooltip: 'Generalization Disjoint-Total', icon: 'imgs/relation/generalization-disjoint-total.png' },	     
	     { name : 'generalization-overlapping-total', model : 'omtgRelation', tooltip: 'Generalization Overlapping-Total', icon: 'imgs/relation/generalization-overlapping-total.png' },
	     { name : 'arc-network', model : 'omtgRelation', tooltip: 'Arc-Node Network', icon: 'imgs/relation/arc-network.png' }
	]);

	
	// List of toolboxes
	app.toolboxes = new app.Toolboxes([
	     {name: "Classes", tools : app.classTools},
	     {name: "Relationships", tools : app.relationshipTools}
	]);	

	// Canvas Model
	app.canvas = new app.Canvas();
	
	// Initialize Backbone views.
	app.bodyView = new app.BodyView();
	app.navbarView = new app.NavbarView({el: $('#navbar')});
	app.toolboxesView = new app.ToolboxesView({el: $('#section-sidebar'), model: app.toolboxes});
	app.canvasView = new app.CanvasView({el: '#canvas', model: app.canvas});
});
