#!/bin/bash 

uglifyjs src/js/app.js src/js/plumb.js src/js/xml-parser.js src/js/models/canvas.js src/js/models/toolbox.js src/js/models/tool.js src/js/models/omtg/attribute.js src/js/models/omtg/diagram.js src/js/collections/toolboxes.js src/js/collections/tools.js src/js/collections/omtg/attributes.js src/js/collections/omtg/diagrams.js src/js/views/about-view.js src/js/views/navbar-view.js src/js/views/toolbox-view.js src/js/views/tool-view.js src/js/views/canvas-view.js src/js/views/toolboxes-view.js src/js/views/tools-view.js src/js/views/xmlimporter-view.js src/js/views/omtg/attribute-view.js src/js/views/omtg/connectioneditor-view.js src/js/views/omtg/diagrameditor-view.js src/js/views/omtg/diagram-view.js -o dist/js/script.js -m

uglifycss src/css/style.css > dist/css/style.css
