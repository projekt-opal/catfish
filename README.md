# Catfish

Data cleaning component.

## Usage with Apache Maven

Add the following lines to your `pom.xml` configuration file:

	<dependencies>
		<dependency>
			<groupId>org.dice-research.opal</groupId>
			<artifactId>catfish</artifactId>
			<version>[1,2)</version>
		</dependency>
	</dependencies>
	
	<repositories>
		<repository>
			<id>maven.aksw.internal</id>
			<name>AKSW Repository</name>
			<url>http://maven.aksw.org/archiva/repository/internal</url>
		</repository>
		<repository>
			<id>maven.aksw.snapshots</id>
			<name>AKSW Snapshot Repository</name>
			<url>http://maven.aksw.org/archiva/repository/snapshots</url>
		</repository>
	</repositories>

Available versions are listed at [maven.aksw.org](https://maven.aksw.org/archiva/#advancedsearch~internal/org.dice-research.opal~catfish~~~~~30).


## Example: Configuration

```Java
import java.io.File;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.dice_research.opal.catfish.Catfish;
import org.dice_research.opal.common.utilities.FileHandler;
import org.dice_research.opal.common.vocabulary.Opal;

public class Example {

  /**
   * Cleans data.
   * 
   * @param turtleInputFile  A TURTLE file to read
   * @param turtleOutputFile A TURTLE file to write results
   * @param datasetUri       A URI of a dcat:Dataset inside the TURTLE data
   * 
   * @see https://www.w3.org/TR/turtle/
   * @see https://www.w3.org/TR/vocab-dcat/
   */
  public void cleanMetadata(File turtleInputFile, File turtleOutputFile, String datasetUri) throws Exception {

	// Load TURTLE file into model
	Model model = FileHandler.importModel(turtleInputFile);

	CleaningConfig cleaningConfig = new CleaningConfig();
	// Remove blank nodes, which are not subject of triples
	// (optional method call, default: true)
	cleaningConfig.setCleanEmptyBlankNodes(true);

	// Remove triples with literals as object, which contain no value or unreadable.
	// And also extract Language Tag and DataType if it is mistakenly inside the string
	// (optional method call, default: true)
	cleaningConfig.setCleanLiterals(true);

	// Check dct:format and dcat:mediaType for values and create new triples.
	// (optional method call, default: true)
	cleaningConfig.setCleanFormats(true);

	// Rewrites date formats
	// (optional method call, default: false)
	cleaningConfig.setEqualizeDateFormats(false);

	// Rewrites URIs of datasets and distributions
	// Catalogs are listed at opal.common.constants.Catalogs
	// (optional method call, default: null)
	cleaningConfig.setCatalogIdToReplaceUris(Catalogs.ID_MCLOUD);

	// Removes datasets, which do not have a german and an english title.
	// Additionally, non-german and non-english titles and descriptions are removed.
	// (optional method call, default: false)
	cleaningConfig.setRemoveNonDeEnTitleDatasets(false);

	Catfish catfish = new Catfish(cleaningConfig);

	// Update model
	catfish.processModel(model, datasetUri);

	// Would be null, if no catalog to replace URIs was set
	datasetUri = catfish.getNewDatasetUri();
	System.out.println("New dataset URI: " + datasetUri);

	// Example for requesting formats
	printFormats(model, datasetUri);

	// Write updated model into TURTLE file
	FileHandler.export(turtleOutputFile, model);
  }
}
```

## Example: Requesting formats

```Java
/**
 * Example for requesting formats.
 * 
 * Generated formats are of type http://projekt-opal.de/Format.
 */
public void printFormats(Model model, String datasetUri) {

  // Go through Distributions of current Dataset
  StmtIterator distributionIterator = model.getResource(datasetUri).listProperties(DCAT.distribution);
  while (distributionIterator.hasNext()) {
    RDFNode rdfNode = distributionIterator.next().getObject();
    if (rdfNode.isResource()) {
      Resource distribution = rdfNode.asResource();

      // Get formats of current Distribution
      StmtIterator formatIterator = distribution.listProperties(DCTerms.format);
      while (formatIterator.hasNext()) {
        RDFNode format = formatIterator.next().getObject();

        // Check if type is http://projekt-opal.de/Format
        if (format.isResource()) {
          Statement statement = format.asResource().getProperty(RDF.type);
          if (statement != null
              && statement.getObject().asResource().getURI().equals(Opal.OPAL_FORMAT.getURI())) {

            // Prints, e.g.
            // http://projekt-opal.de/format/pdf
            // http://projekt-opal.de/format/html
            System.out.println(format);
          }
        }
      }
    }
  }
}
```


## Credits

[Data Science Group (DICE)](https://dice-research.org/) at [Paderborn University](https://www.uni-paderborn.de/)

This work has been supported by the German Federal Ministry of Transport and Digital Infrastructure (BMVI) in the project [Open Data Portal Germany (OPAL)](http://projekt-opal.de/) (funding code 19F2028A).
