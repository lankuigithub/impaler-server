package site.lankui.impaler.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.ObjectUtils;
import site.lankui.impaler.constant.SessionType;
import site.lankui.impaler.constant.StatusType;

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

	public Session getSession(SessionType sessionType) {
		return sessionMap.get(sessionType);
	}

	public boolean containsSession(SessionType sessionType) {
		return sessionMap.containsKey(sessionType);
	}

	public void addSession(Session session) {
		if(!ObjectUtils.isEmpty(session)){
			session.setClient(this);
			sessionMap.put(session.getType(), session);
			this.status = StatusType.ON;
		}
	}

	public void removeSession(Session session) {
		if(!ObjectUtils.isEmpty(session)) {
			sessionMap.remove(session.getType());
		}
		if (sessionMap.isEmpty()) {
			this.status = StatusType.OFF;
		}
	}

}
