package site.lankui.impaler.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import site.lankui.impaler.bean.Client;
import site.lankui.impaler.bean.ClientManager;
import site.lankui.impaler.bean.Session;
import site.lankui.impaler.command.Command;
import site.lankui.impaler.command.CommandDefine;
import site.lankui.impaler.constant.ImpalerConstant;
import site.lankui.impaler.constant.StatusType;
import site.lankui.impaler.util.Generator;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
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
	private ClientManager clientManager;

	private static final int MAX_THREAD_NUM = 5;
	private ExecutorService executorService;

	@PostConstruct
	public void init() {
		executorService = Executors.newFixedThreadPool(MAX_THREAD_NUM);
	}

	public void execute(Command sourceCommand, Session sourceSession) {
		executorService.execute(() -> {
			switch (sourceCommand.getType()) {
				case PING:
					sourceSession.getChannel().writeAndFlush(CommandDefine.COMMAND_PONG);
					break;
				case REGISTER_REQUEST:
					String clientName = new String(sourceCommand.getData(), CharsetUtil.UTF_8);
					int clientId = Generator.clientId(clientName);
					Client client = Client.builder()
						.clientId(clientId)
						.name(clientName)
						.status(StatusType.ON)
						.sessionMap(new HashMap<>())
						.build();
					client.addSession(sourceSession);
					clientManager.addClient(client);
					sourceSession.getChannel().writeAndFlush(
						CommandDefine.generateCommand(
							CommandDefine.REGISTER_RESPONSE,
							ImpalerConstant.CLIENT_ID_NONE,
							clientId
						)
					);
					break;
				case CLIENT_LIST_REQUEST:
					try {
						List<Client> clientList = new ArrayList<>();
						for(Map.Entry<Integer, Client> entry: clientManager.getClientMap().entrySet()) {
							clientList.add(entry.getValue());
						}
						ObjectMapper objectMapper = new ObjectMapper();
						String json = objectMapper.writeValueAsString(clientList);
						sourceSession.getChannel().writeAndFlush(
							CommandDefine.generateCommand(
								CommandDefine.CLIENT_LIST_RESPONSE,
								ImpalerConstant.CLIENT_ID_NONE,
								json
							)
						);
					} catch (JsonProcessingException e) {
						log.error("generate client list json error");
					}
					break;
				default:
					if(sourceCommand.getTarget() == ImpalerConstant.CLIENT_ID_NONE) {
						sourceCommand.setTarget(sourceSession.getClient().getClientId());
						for (Map.Entry<Integer, Client> entry : clientManager.getClientMap().entrySet()) {
							Client targetClient = entry.getValue();
							if(targetClient.getClientId() == sourceSession.getClient().getClientId()) {
								continue;
							}
							if(targetClient.containsSession(sourceSession.getType())) {
								targetClient.getSession(sourceSession.getType())
									.getChannel()
									.writeAndFlush(sourceCommand);
							}
						}
					} else {
						if(clientManager.containsClient(sourceCommand.getTarget())) {
							Session targetSession = clientManager.getClient(sourceCommand.getTarget()).getSession(sourceSession.getType());
							if(!ObjectUtils.isEmpty(targetSession)) {
								sourceCommand.setTarget(sourceSession.getClient().getClientId());
								targetSession.getChannel().writeAndFlush(sourceCommand);
							}
						}
					}
					sourceSession.getChannel().writeAndFlush(CommandDefine.COMMAND_OK);
					break;
			}
		});
	}

}
