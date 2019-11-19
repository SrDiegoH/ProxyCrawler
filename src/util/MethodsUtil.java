package util;

public class MethodsUtil {

	public static String getSubstring(String text, String startText, String endText, boolean removeTags){				
		int startIndex = text.indexOf(startText);
		
		int startIndexLength = startText.length();		
		
		String newText = text.substring(startIndex);
		
		int endIndex = newText.substring(startIndexLength).indexOf(endText) + startIndexLength;
		
		newText = newText.substring(startIndexLength, endIndex);
		
		if(removeTags)
			newText = newText.replaceAll("<[^>]*>", "");
		
		return newText.trim();
	}
	
	
	public static String nullOrEmptyToOther(String value, String replace) {
		return isEmpty(value)? replace : value;
	}
	
	public static boolean isEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}
	
	public static String stringNumberToDouble(String value) {
		if(!isEmpty(value))
			return value.replace(".", "").replace(",", ".");
		
		return "0.0";
	}
}
