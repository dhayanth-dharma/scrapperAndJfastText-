package com.aitenders.service;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.aitenders.enums.AitendersLanguage;


@Component
public class StopWordComponent {
	private static final Logger logger = LoggerFactory.getLogger(StopWordComponent.class);

	
	private String stopWordDirectory;
	private final Map<AitendersLanguage, String> stopWordsCache = new HashMap<>();
	/**
	 * @param str the full text that intended to exempt stopping words
	 * @return String without stopping words Removes stopping words from given
	 *         string and returns new string .
	 */
	public String filterStopWords(final String str, final AitendersLanguage language) {
		String correctedString = str;
		if (language == AitendersLanguage.FRENCH) {
			correctedString = correctedString.replaceAll("\\s[a-zA-Z]['’]", " ");
		} else if (language == AitendersLanguage.ENGLISH) {
			correctedString = correctedString.replaceAll("['’]s\\s", " ");
			correctedString = correctedString.replaceAll("['’]s(\\.|,|\\!|\\?|;)", ".");
		}
		try {
			final String regex = readStopWordsAsRegex(language, "src/main/resources/stopword/");
			final Pattern pattern = Pattern.compile("\\b" + regex + "\\b", Pattern.CASE_INSENSITIVE);
			final Matcher match = pattern.matcher(correctedString);
			correctedString = match.replaceAll("");
			return correctedString;
		} catch (final IOException e) {
			logger.error("Failed to extract stop words", e);
			return correctedString;
		}
	}

	/**
	 * Clean the word by removing meaningless characters (depending of language)
	 *
	 * @param word
	 * @param language
	 * @return the cleaned word or <code>null</code> if it is a stopWOrd
	 */
	public String tryStopWord(final String word, final AitendersLanguage language, final String stopWordDir) {
		String correctedString = word.trim();
		if (language == AitendersLanguage.FRENCH) {
			correctedString = correctedString.replaceAll("\\s[a-zA-Z]['’]", " ");
		} else if (language == AitendersLanguage.ENGLISH) {
			correctedString = correctedString.replaceAll("['’]s\\s", " ");
			correctedString = correctedString.replaceAll("['’]s(\\.|,|\\!|\\?|;)", ".");
		}
		try {
			
			final String regex = readStopWordsAsRegex(language, stopWordDir);
			final Pattern pattern = Pattern.compile("\\b" + regex + "\\b\\s?", Pattern.CASE_INSENSITIVE);
			final Matcher match = pattern.matcher(correctedString);
			correctedString = match.replaceAll("");
			correctedString = correctedString.trim();
			return correctedString.isEmpty() ? null : correctedString;
		} catch (final IOException e) {
			logger.error("Failed to extract stop words", e);
			return correctedString;
		}
	}

	/**
	 * Get Stop Words from the txt file based on given language
	 *
	 * @throws FileNotFoundException
	 */
	private String readStopWordsAsRegex(final AitendersLanguage language, String dir) throws IOException {
		if (!stopWordsCache.containsKey(language)) {
			stopWordDirectory="src/main/resources/stopword/";
			
			final Path stopWordFile = Paths.get(stopWordDirectory, "stopword." + language.nluValue + ".txt");
			if (Files.notExists(stopWordFile)) {
				throw new FileNotFoundException(stopWordFile.toFile().getAbsolutePath());
			}
			final StringBuilder sb = new StringBuilder("(");
			boolean isFirst = true;
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(stopWordFile.toFile()), StandardCharsets.UTF_8))) {
				String line;
				while ((line = br.readLine()) != null) {
					if (line.trim().isEmpty()) {
						continue;
					}
					if (isFirst) {
						isFirst = false;
					} else {
						sb.append("|");
					}
					sb.append(line);
				}
			}
			sb.append(")");
			stopWordsCache.put(language, sb.toString());
		}

		return stopWordsCache.get(language);

	}
}
