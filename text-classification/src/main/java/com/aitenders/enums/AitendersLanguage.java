package com.aitenders.enums;

public enum AitendersLanguage {
	FRENCH("fr"), GERMAN("de"), ENGLISH("en");

	public final String nluValue;

	private AitendersLanguage(final String nluValue) {
		this.nluValue = nluValue;
	}

	public String getNluValue() {
		return this.nluValue;
	}

	public static AitendersLanguage fromString(final String str) {
		for (final AitendersLanguage language : AitendersLanguage.values()) {
			if (language.name().equalsIgnoreCase(str) || language.nluValue.equalsIgnoreCase(str)) {
				return language;
			}
		}
		return null;
	}

}