package org.dice_research.opal.catfish.service;

import org.apache.jena.rdf.model.Model;

public interface Cleanable {
    void clean(Model model);
}
