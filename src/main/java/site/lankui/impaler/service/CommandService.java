package site.lankui.impaler.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import site.lankui.impaler.bean.Client;
import site.lankui.impaler.bean.ClientManager;
import site.lankui.impaler.bean.Session;
import site.lankui.impaler.bean.SessionManager;
import site.lankui.impaler.command.Command;
import site.lankui.impaler.command.CommandDefine;
import site.lankui.impaler.util.Generator;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static site.lankui.impaler.command.CommandDefine.*;

@Service
@Slf4j
@Scope(value = BeanDefinition.SCOPE_SINGLETON)
public class CommandService {

	@Autowired
	private SessionManager sessionManager;
	@Autowired
	private ClientManager clientManager;

	private static final int MAX_THREAD_NUM = 5;
	private ExecutorService executorService;

	@PostConstruct
	public void init() {
		executorService = Executors.newFixedThreadPool(MAX_THREAD_NUM);
	}

	public void execute(Command command, Session session) {
		executorService.execute(() -> {
			switch (command.getType()) {
				case PING:
					session.getChannel().writeAndFlush(CommandDefine.COMMAND_PONG);
					break;
				case REGISTER:
					String clientName = new String(command.getData(), CharsetUtil.UTF_8);
					Client client = Client.builder()
						.clientId(Generator.clientId(clientName))
						.name(clientName)
						.build();
					clientManager.addClient(client);
					break;
				case CLIENT_LIST_REQUEST:
					try {
						List<Client> clientList = new ArrayList<>();
						for(Map.Entry<Integer, Client> entry: clientManager.getClientMap().entrySet()) {
							clientList.add(entry.getValue());
						}
						ObjectMapper objectMapper = new ObjectMapper();
						String json = objectMapper.writeValueAsString(clientList);
						Command clientListCommand = Command.builder()
							.type(CLIENT_LIST_RESPONSE)
							.target(command.getTarget())
							.dataLength(json.getBytes().length)
							.data(json.getBytes(CharsetUtil.UTF_8))
							.build();
						session.getChannel().writeAndFlush(clientListCommand);
					} catch (JsonProcessingException e) {
						log.error("generate client list json error");
					}
					break;
				default:
					for (Map.Entry<String, Session> entry : sessionManager.getSessionMap().entrySet()) {
						if (!entry.getKey().equals(session.getSessionId())) {
							entry.getValue().getChannel().writeAndFlush(command);
						}
					}
					session.getChannel().writeAndFlush(CommandDefine.COMMAND_OK);
					break;
			}
		});
	}

}
