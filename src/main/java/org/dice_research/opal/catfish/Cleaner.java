package org.dice_research.opal.catfish;

import org.apache.jena.rdf.model.Model;

public interface Cleaner {
    void clean(Model model);
}
