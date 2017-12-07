package site.lankui.impaler.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
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

	public static <T> T stringToObject(String string, Class<T> clazz ) {
		T t;
		try {
			t =  objectMapper.readValue(string, clazz);
		} catch (IOException e) {
			t = null;
		}
		return t;
	}

	public static <T> T bytesToObject(byte[] bytes, Class<T> clazz) {
		T message;
		String string = new String(bytes, CharsetUtil.UTF_8);
		try {
			message =  objectMapper.readValue(string, clazz);
		} catch (IOException e) {
			log.error("bytesToObject error: ", e);
			message = null;
		}
		return message;
	}
}
