package site.lankui.impaler.client.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ObjectUtils;
import site.lankui.impaler.constant.SessionType;
import site.lankui.impaler.constant.StatusType;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@Builder
public class Client {

	private int clientId;

	private String name;

	private StatusType status;

	@JsonIgnore
	private Map<SessionType, Session> sessionMap;

}
