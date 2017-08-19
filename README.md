# EcoreMetamodelExtraction
This project is a reverse engineering tool for Ecore metamodels. It allows extracting Ecore metamodels from any arbitrary Java code. At the core of the approach is a mapping from elements of the implicit Java metamodel, which the [Java Language Specification](https://docs.oracle.com/javase/specs/jls/se8/html/index.html) defines, to elements of the Ecore meta-metamodel. The project makes heavy use of the [EMF Ecore](http://download.eclipse.org/modeling/emf/emf/javadoc/2.9.0/org/eclipse/emf/ecore/package-summary.html) and the [Eclipse JDT](https://help.eclipse.org/oxygen/index.jsp?topic=%2Forg.eclipse.jdt.doc.isv%2Freference%2Fapi%2Foverview-summary.html) API.

Open Java projects in the Eclipse IDE can be selected to extract an Ecore Metamodel, which can be saved as an Ecore file. See the [wiki](https://github.com/tsaglam/EcoreMetamodelExtraction/wiki) for more details.

## How to install:
1. Clone or download the project
2. Import as existing project into the Eclipse IDE
3. You need the Eclipse Modeling Framework and the Eclipse Java Development Tools, make sure that both are installed.
4. Run the project as Eclipse Application.
5. You can start the extraction from the EME menu in the menubar or from the toolbar (provisional UI).
6. Extracting metamodels from projects with errors can cause problems while resolving types. It is recommended to use code that compiles without problems.
