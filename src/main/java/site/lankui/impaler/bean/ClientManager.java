package site.lankui.impaler.bean;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Scope(value = BeanDefinition.SCOPE_SINGLETON)
@Slf4j
public class ClientManager {

	@Setter
	@Getter
	private Map<Integer, Client> clientMap;

	ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

	@PostConstruct
	public void init() {
		clientMap = new HashMap<>();
		executorService.scheduleAtFixedRate(() -> {
			log.info("Clear client...");
			for(Map.Entry<Integer, Client> entry : clientMap.entrySet()) {
				if(entry.getValue().getSessionMap().isEmpty()){
					clientMap.remove(entry.getKey());
				}
			}
		}, 0, 60 * 10, TimeUnit.SECONDS);
	}

	public Client getClient(int clientId) {
		return clientMap.get(clientId);
	}

	public boolean containsClient(int clientId) {
		return clientMap.containsKey(clientId);
	}

	public void addClient(Client client) {
		if(!ObjectUtils.isEmpty(client)){
			clientMap.put(client.getClientId(), client);
		}
	}

	public void removeClient(Client client) {
		if(!ObjectUtils.isEmpty(client)) {
			clientMap.remove(client.getClientId());
		}
	}
}
