package site.lankui.impaler.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {
	private static ObjectMapper objectMapper = new ObjectMapper();

	public static String objectToString(Object object) {
		String json;
		try {
			json = objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			json = "";
		}
		return json;
	}
}
