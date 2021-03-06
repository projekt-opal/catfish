package org.dice_research.opal.catfish.config;

public class CleaningConfig {
	
	private boolean cleanEmptyBlankNodes = true;
	private boolean removeNonDeEnEmptyTitleLiterals = false;
	private boolean removeNonDeEnTitleDatasets = false;
	private boolean cleanLiterals = true;
	private boolean cleanFormats = true;
	private boolean equalizeDateFormats = false;
	private String catalogIdToReplaceUris = null;

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

	public String getCatalogIdToReplaceUris() {
		return catalogIdToReplaceUris;
	}

	public CleaningConfig setCatalogIdToReplaceUris(String catalogIdToReplaceUris) {
		this.catalogIdToReplaceUris = catalogIdToReplaceUris;
		return this;
	}

	public boolean isRemovingNonDeEnTitleDatasets() {
		return removeNonDeEnTitleDatasets;
	}

	public CleaningConfig setRemoveNonDeEnTitleDatasets(boolean removeNonDeEnTitleDatasets) {
		this.removeNonDeEnTitleDatasets = removeNonDeEnTitleDatasets;
		return this;
	}

	public boolean isRemovingNonDeEnEmptyTitleLiterals() {
		return removeNonDeEnEmptyTitleLiterals;
	}

	public CleaningConfig setRemoveNonDeEnEmptyTitleLiterals(boolean removeNonDeEnEmptyTitleLiterals) {
		this.removeNonDeEnEmptyTitleLiterals = removeNonDeEnEmptyTitleLiterals;
		return this;
	}
}
