package org.dice_research.opal.catfish.utility;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

public class JenaModelUtilities {
    public static Model getModelCopy(Model model) {
        Model ret = ModelFactory.createDefaultModel();
        ret.add(model);
        return ret;
    }
}
