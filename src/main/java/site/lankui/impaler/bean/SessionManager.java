package site.lankui.impaler.bean;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@Scope(value = BeanDefinition.SCOPE_SINGLETON)
public class SessionManager {

	@Setter
	@Getter
	private Map<String, Session> sessionMap = new HashMap<>();

	public void addSession(Session session) {
		if (!ObjectUtils.isEmpty(session)) {
			sessionMap.put(session.getSessionId(), session);
		}
	}

	public void removeSession(Session session) {
		if (!ObjectUtils.isEmpty(session)) {
			sessionMap.remove(session.getSessionId());
		}
	}

	public Session getSession(String sessionId) {
		return sessionMap.get(sessionId);
	}
}
