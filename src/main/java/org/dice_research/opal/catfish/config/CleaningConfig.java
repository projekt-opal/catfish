package org.dice_research.opal.catfish.config;

public class CleaningConfig {
    private boolean cleanEmptyBlankNodes = true;
    private boolean cleanFormats = true;
    private boolean cleanThemes = true;
    private boolean cleanLiterals = true;

    public boolean isCleanEmptyBlankNodes() {
        return cleanEmptyBlankNodes;
    }

    public CleaningConfig setCleanEmptyBlankNodes(boolean cleanEmptyBlankNodes) {
        this.cleanEmptyBlankNodes = cleanEmptyBlankNodes;
        return this;
    }

    public boolean isCleanFormats() {
        return cleanFormats;
    }

    public CleaningConfig setCleanFormats(boolean cleanFormats) {
        this.cleanFormats = cleanFormats;
        return this;
    }

    public boolean isCleanThemes() {
        return cleanThemes;
    }

    public CleaningConfig setCleanThemes(boolean cleanThemes) {
        this.cleanThemes = cleanThemes;
        return this;
    }

    public boolean isCleanLiterals() {
        return cleanLiterals;
    }

    public CleaningConfig setCleanLiterals(boolean cleanLiterals) {
        this.cleanLiterals = cleanLiterals;
        return this;
    }
}
