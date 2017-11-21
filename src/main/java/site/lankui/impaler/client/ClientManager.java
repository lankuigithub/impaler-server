package site.lankui.impaler.client;

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
public class ClientManager {

	@Setter
	@Getter
	private Map<String, Client> clientMap = new HashMap<>();

	public void addClient(Client client) {
		if (!ObjectUtils.isEmpty(client)) {
			clientMap.put(client.getClientId(), client);
		}
	}

	public void removeClient(Client client) {
		if (!ObjectUtils.isEmpty(client)) {
			clientMap.remove(client.getClientId());
		}
	}

	public Client getClient(String clientId) {
		return clientMap.get(clientId);
	}
}
