package site.lankui.impaler.client;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import site.lankui.impaler.client.bean.Client;
import site.lankui.impaler.client.bean.Session;
import site.lankui.impaler.constant.SessionType;
import site.lankui.impaler.constant.StatusType;
import site.lankui.impaler.server.ServerManager;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Scope(value = BeanDefinition.SCOPE_SINGLETON)
@Slf4j
public class ConnectManager {

	@Setter
	@Getter
	private Map<Integer, Client> clientMap;

	@Autowired
	private ServerManager serverManager;

	private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

	@PostConstruct
	public void init() {
		clientMap = new HashMap<>();
		executorService.scheduleAtFixedRate(() -> {
			boolean removeData = false;
			for(Map.Entry<Integer, Client> entry : clientMap.entrySet()) {
				if(entry.getValue().getSessionMap().isEmpty()){
					removeClient(entry.getValue());
					removeData = true;
				}
			}
			if (removeData) {
				serverManager.pushClientListFilter();
			}
		}, 0, 60, TimeUnit.SECONDS);
	}

	public Client getClient(int clientId) {
		return clientMap.get(clientId);
	}

	public void addClient(Client client, Session session) {
		if (clientMap.containsKey(client.getClientId())) {
			client = clientMap.get(client.getClientId());
		} else {
			clientMap.put(client.getClientId(), client);
		}
		addSession(client, session);
		// set client status
		client.setStatus(StatusType.ON);
		// push client list
		serverManager.pushClientListFilter(client);
	}

	public void removeClient(Client client) {
		if(!ObjectUtils.isEmpty(client)) {
			clientMap.remove(client.getClientId());
		}
	}

	public Session getSession(Client client, SessionType sessionType) {
		return client.getSessionMap().get(sessionType);
	}

	public void removeSession(Session session) {
		if (ObjectUtils.isEmpty(session.getClient())) {
			return;
		}
		Map<SessionType, Session> sessionMap = session.getClient().getSessionMap();
		sessionMap.remove(session.getType());
		if (!sessionMap.isEmpty()) {
			return;
		}
		// set client status
		session.getClient().setStatus(StatusType.OFF);
		// push client list
		serverManager.pushClientListFilter();

	}

	private void addSession(Client client, Session session) {
		Map<SessionType, Session> sessionMap = client.getSessionMap();
		sessionMap.put(session.getType(), session);
		session.setClient(client);
	}

}
