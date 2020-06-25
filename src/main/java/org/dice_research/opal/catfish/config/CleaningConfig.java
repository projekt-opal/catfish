package org.dice_research.opal.catfish.config;

public class CleaningConfig {
	private boolean cleanEmptyBlankNodes = true;
	private boolean cleanFormats = true;
	private boolean cleanLiterals = true;
	private boolean equalizeDateFormats = false;

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

	public boolean isCleanLiterals() {
		return cleanLiterals;
	}

	public CleaningConfig setCleanLiterals(boolean cleanLiterals) {
		this.cleanLiterals = cleanLiterals;
		return this;
	}

	public boolean isEqualizingDateFormats() {
		return equalizeDateFormats;
	}

	public CleaningConfig setEqualizeDateFormats(boolean equalizeDateFormats) {
		this.equalizeDateFormats = equalizeDateFormats;
		return this;
	}
}
