package util;

import java.lang.reflect.Field;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONHelper {
	public static <T> HashMap<String, String> toHashMap(T object){
		HashMap<String, String> hash = new HashMap<>();
		
		Class<T> actualClass = (Class<T>) object.getClass();
		if(actualClass != null) {
			for (Field field : actualClass.getDeclaredFields()) {
				try {
					if(field != null) {
						field.setAccessible(true);
						hash.put(field.getName(), String.valueOf(field.get(object)));
					}
				} catch (Exception e) { }
			}
		}
		
		return hash;
	}
	
	public static <T> String hashMapToJsonString(HashMap<String, T> options) {		
		Gson gsonParser = new GsonBuilder().disableInnerClassSerialization().create();
		
		String jsonString = gsonParser.toJson(options);
		
		return jsonString;
	}
}
