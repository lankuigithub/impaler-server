package site.lankui.impaler.bean;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@Builder
public class Message {
	private int code;
	private String msg;
	private Map<String, Object> data;

	public static Message successMessage(Map<String, Object> data) {
		return Message.builder()
			.code(0)
			.msg("请求成功")
			.data(data)
			.build();
	}
}
