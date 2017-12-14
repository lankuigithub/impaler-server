package site.lankui.impaler.client;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import site.lankui.impaler.client.bean.Client;
import site.lankui.impaler.client.bean.Session;
import site.lankui.impaler.command.Command;
import site.lankui.impaler.command.CommandDefine;
import site.lankui.impaler.command.Message;
import site.lankui.impaler.constant.ImpalerConstant;
import site.lankui.impaler.constant.SessionType;
import site.lankui.impaler.constant.StatusType;
import site.lankui.impaler.util.JsonUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Scope(value = BeanDefinition.SCOPE_SINGLETON)
@Slf4j
public class ConnectManager {

	@Setter
	@Getter
	private Map<Integer, Client> clientMap;

	@PostConstruct
	public void init() {
		clientMap = new HashMap<>();
	}

	public Client getClient(int clientId) {
		return clientMap.get(clientId);
	}

	public void registerClient(Client client, Session session) {
		Integer clientId = client.getClientId();
		if (clientMap.containsKey(clientId)) {
			client = clientMap.get(clientId);
		} else {
			clientMap.put(clientId, client);
		}
		client.addSession(session);
		// push client list
		pushClientListFilter(session.getType(), client);
	}

	public void unRegisterClient(Client client, Session session) {
		if (ObjectUtils.isEmpty(client)) {
			return;
		}
		client.removeSession(session);
		// push client list
		pushClientListFilter(session.getType());
	}

	public void pushClientListFilter(SessionType sessionType, Client... clients) {
		List<Client> clientList = new ArrayList<>();
		for(Map.Entry<Integer, Client> entry: clientMap.entrySet()) {
			clientList.add(entry.getValue());
		}
		Map<String, Object> data = new HashMap<>();
		data.put("name", "客户端列表");
		data.put("list", clientList);
		Command command = CommandDefine.generateCommand(
			CommandDefine.COMMAND_CLIENT_LIST,
			ImpalerConstant.CLIENT_ID_NONE,
			JsonUtils.objectToString(Message.successMessage(data))
		);
		List<Integer> filterClientIdList = Arrays.stream(clients).map(Client::getClientId).collect(Collectors.toList());
		clientList.stream()
			.filter(client -> !filterClientIdList.contains(client.getClientId()))
			.forEach(client -> {
				Session session = client.getSession(sessionType);
				if(!ObjectUtils.isEmpty(session)) {
					session.sendCommand(command);
				}
			});
	}

}
