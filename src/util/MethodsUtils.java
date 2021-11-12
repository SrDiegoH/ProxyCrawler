package util;

import com.google.gson.GsonBuilder;

import java.util.Map;

public class MethodsUtils {

	public static String getSubstring(
			final String text,
			final String startText,
			final String endText,
			final boolean removeTags
	){
		final String newText = cutStringInTheBegin(text, text.indexOf(startText));

		final int endIndex = getIndexOfEndText(newText, startText, endText);

		final String cuttedText = cutStringInTheMiddle(newText, startText.length(), endIndex);

		return removeTags(cuttedText, removeTags).trim();
	}

	private static String cutStringInTheBegin(final String text, final int startIndex){
		return text.substring(startIndex);
	}

	private static String cutStringInTheMiddle(final String text, final int startIndex, final int endIndex){
		return text.substring(startIndex, endIndex);
	}

	private static int getIndexOfEndText(final String text, final String startText, final String endText){
		return cutStringInTheBegin(text, startText.length()).indexOf(endText) + startText.length();
	}

	private static String removeTags(final String text, final boolean removeTags){
		return removeTags? text.replaceAll("<[^>]*>", "") : text;
	}

	public static boolean isEmpty(final String value) {
		return value == null || value.trim().isEmpty();
	}

	public static <T> String convertMapToJson(final Map<String, T> options) {
		return new GsonBuilder()
				.disableInnerClassSerialization()
				.create()
				.toJson(options);
	}
}