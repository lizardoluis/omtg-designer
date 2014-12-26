'use strict';

var Toolboxes = Backbone.Collection.extend({
	model : Toolbox
});

//Diagrams
var diagramsTools = new Tools([
     { name : 'polygon', type : 'georeferenced', tooltip: 'Polygon', icon: 'imgs/omtg/polygon.png' },
     { name : 'line', type : 'georeferenced', tooltip: 'Line', icon: 'imgs/omtg/line.png' },	
     { name : 'point', type : 'georeferenced', tooltip: 'Point', icon: 'imgs/omtg/point.png' },
     { name : 'node', type : 'georeferenced', tooltip: 'Node', icon: 'imgs/omtg/node.png' },
     { name : 'isolines', type : 'georeferenced', tooltip: 'Isolines', icon: 'imgs/omtg/isolines.png' },
     { name : 'planar-subdivision', type : 'georeferenced', tooltip: 'Planar Subdivision', icon: 'imgs/omtg/planar-subdivision.png' },
     { name : 'TIN', type : 'georeferenced', tooltip: 'Triangular Irregular Network', icon: 'imgs/omtg/TIN.png' },
     { name : 'tesselation', type : 'georeferenced', tooltip: 'Tesselation', icon: 'imgs/omtg/tesselation.png' },
     { name : 'sample', type : 'georeferenced', tooltip: 'Sample', icon: 'imgs/omtg/sample.png' },
     { name : 'un-line', type : 'georeferenced', tooltip: 'Unidirectional Line', icon: 'imgs/omtg/un-line.png' },
     { name : 'bi-line', type : 'georeferenced', tooltip: 'Bidirectional Line', icon: 'imgs/omtg/bi-line.png' },
     { name : 'conventional', type : 'conventional', tooltip: 'Conventional', icon: 'imgs/omtg/conventional.png' }
]);

// Relations
var relationsTools = new Tools([
     { name : 'aggregation', type : 'relation', tooltip: 'Aggregation', icon: 'imgs/relation/aggregation.png' },
     { name : 'association', type : 'relation', tooltip: 'Association', icon: 'imgs/relation/association.png' },
     { name : 'cartographic-generalization-overlapping', type : 'relation', tooltip: 'Cartographic Generalization Overlapping', icon: 'imgs/relation/cartographic-generalization-overlapping.png' },
     { name : 'generalization-disjoint-partial', type : 'relation', tooltip: 'Generalization Disjoint-Partial', icon: 'imgs/relation/generalization-disjoint-partial.png' },
     { name : 'generalization-disjoint-total', type : 'relation', tooltip: 'Generalization Disjoint-Total', icon: 'imgs/relation/generalization-disjoint-total.png' },
     { name : 'generalization-overlapping-partial', type : 'relation', tooltip: 'Generalization Overlapping-Partial', icon: 'imgs/relation/generalization-overlapping-partial.png' },
     { name : 'generalization-overlapping-total', type : 'relation', tooltip: 'Generalization Overlapping-Total', icon: 'imgs/relation/generalization-overlapping-total.png' },
     { name : 'spatial-aggregation', type : 'relation', tooltip: 'Spatial Aggregation', icon: 'imgs/relation/spatial-aggregation.png' },
     { name : 'spatial-association', type : 'relation', tooltip: 'Spatial Association', icon: 'imgs/relation/spatial-association.png' },
     { name : 'arc-network', type : 'relation', tooltip: 'Arc Network', icon: 'imgs/relation/arc-network.png' }
]);

// List of toolboxes
var toolboxes = new Toolboxes([
     {name: "Diagrams", tools : diagramsTools},
     {name: "Relations", tools : relationsTools}
]);
