# EcoreMetamodelExtraction
This project extracts Ecore metamodels from Java code. It allows to select from the open Java projects in the Eclipse IDE to extract an Ecore Metamodel, which is saved in a new EMF modeling project as an Ecore file. See the [wiki](https://github.com/tsaglam/EcoreMetamodelExtraction/wiki) for more details.

## How to install:
1. Clone or download the project
2. Import as existing project into the Eclipse IDE
3. You need the Eclipse Modeling Framework and the Eclipse Java Development Tools, make sure that both are installed.
4. Run the project as Eclipse Application
5. You can start the extraction from the EME menu in the menubar or from the toolbar.
6. Extracting metamodels from projects with errors can cause problems while resolving types. It is recommended to use code that compiles without problems.