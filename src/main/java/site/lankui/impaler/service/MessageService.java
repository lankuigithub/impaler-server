package site.lankui.impaler.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import site.lankui.impaler.client.Client;
import site.lankui.impaler.client.ClientManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Scope(value = BeanDefinition.SCOPE_SINGLETON)
public class MessageService {

	private final static int MAX_THREAD_COUNT = 10;

	@Autowired
	private ClientManager clientManager;

	private ExecutorService executorService;

	public MessageService() {
		executorService = Executors.newFixedThreadPool(MAX_THREAD_COUNT);
	}

	public void sendNotExistsSelf(Client client, String message) {
		executorService.submit(() -> clientManager.getClientMap().keySet()
			.stream()
			.filter(clientId ->
				!clientId.equals(client.getClientId()))
			.forEach(clientId ->
				clientManager.getClient(clientId).getChannel().writeAndFlush(message)
			)
		);
	}
}