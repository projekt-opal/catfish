package org.dice_research.opal.catfish;

import org.apache.jena.rdf.model.Model;
import org.dice_research.opal.catfish.service.impl.FormatCleaner;
import org.dice_research.opal.catfish.service.impl.LiteralCleaner;
import org.dice_research.opal.catfish.service.impl.StructuralCleaner;
import org.dice_research.opal.catfish.service.impl.ThemeCleaner;
import org.dice_research.opal.common.interfaces.JenaModelProcessor;
import org.dice_research.opal.common.interfaces.ModelProcessor;

/**
 * OPAL RDF and DCAT cleaning component.
 *
 * @author Adrian Wilke
 */
@SuppressWarnings("deprecation")
public class Catfish implements ModelProcessor, JenaModelProcessor {

    private boolean removeEmptyBlankNodes = true;
    private boolean removeEmptyLiterals = true;
    private boolean cleanFormats = true;
    private boolean cleanThemes = true;
    private boolean cleanLiterals = true;

    @Override
    public void processModel(Model model, String datasetUri) {

        // Clean structural contents, e.g. empty values
        if (isRemovingEmptyBlankNodes() || isRemovingEmptyLiterals()) {
            new StructuralCleaner(this).clean(model);
        }

        // Clean formats and mediaTypes
        if (isCleaningFormats()) {
            new FormatCleaner().clean(model, model.getResource(datasetUri));
        }

        if(cleanThemes) // TODO: 4/1/20 Make an array of Cleaner interface, and call them in a row
            new ThemeCleaner().clean(model);

        if(cleanThemes) // TODO: 4/1/20 Make an array of Cleaner interface, and call them in a row
            new LiteralCleaner().clean(model);

    }

    public boolean isRemovingEmptyBlankNodes() {
        return removeEmptyBlankNodes;
    }

    public boolean isRemovingEmptyLiterals() {
        return removeEmptyLiterals;
    }

    public boolean isCleaningFormats() {
        return cleanFormats;
    }

    public Catfish removeEmptyBlankNodes(boolean removeEmptyBlankNodes) {
        this.removeEmptyBlankNodes = removeEmptyBlankNodes;
        return this;
    }

    public Catfish removeEmptyLiterals(boolean removeEmptyLiterals) {
        this.removeEmptyLiterals = removeEmptyLiterals;
        return this;
    }

    public Catfish cleanFormats(boolean cleanFormats) {
        this.cleanFormats = cleanFormats;
        return this;
    }

    /**
     * @deprecated Replaced by {@link #processModel(Model, String)}.
     */
    @Deprecated
    @Override
    public Model process(Model model, String datasetUri) throws Exception {
        processModel(model, datasetUri);
        return model;
    }
}