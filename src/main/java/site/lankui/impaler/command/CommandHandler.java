package site.lankui.impaler.command;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import site.lankui.impaler.bean.Client;
import site.lankui.impaler.bean.ClientManager;
import site.lankui.impaler.bean.Session;
import site.lankui.impaler.constant.ImpalerConstant;
import site.lankui.impaler.constant.StatusType;
import site.lankui.impaler.util.Generator;
import site.lankui.impaler.util.SpringBeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class CommandHandler {

	private ClientManager clientManager;
	public static final int defaultCommandType = -1;

	public CommandHandler() {
		clientManager = SpringBeanUtils.getBean(ClientManager.class);
	}

	@CommandMethod(type = CommandDefine.PING)
	public void handlePing(Command command, Session session) {
		session.getChannel().writeAndFlush(CommandDefine.COMMAND_PONG);
	}

	@CommandMethod(type = CommandDefine.REGISTER_REQUEST)
	public void handleRegister(Command command, Session session) {
		String clientName = new String(command.getData(), CharsetUtil.UTF_8);
		int clientId = Generator.clientId(clientName);
		Client client = Client.builder()
			.clientId(clientId)
			.name(clientName)
			.status(StatusType.ON)
			.sessionMap(new HashMap<>())
			.build();
		client.addSession(session);
		clientManager.addClient(client);
		session.getChannel().writeAndFlush(
			CommandDefine.generateCommand(
				CommandDefine.REGISTER_RESPONSE,
				ImpalerConstant.CLIENT_ID_NONE,
				clientId
			)
		);
	}

	@CommandMethod(type = CommandDefine.CLIENT_LIST_REQUEST)
	public void handleClientList(Command command, Session session) {
		try {
			List<Client> clientList = new ArrayList<>();
			for(Map.Entry<Integer, Client> entry: clientManager.getClientMap().entrySet()) {
				clientList.add(entry.getValue());
			}
			ObjectMapper objectMapper = new ObjectMapper();
			String json = objectMapper.writeValueAsString(clientList);
			session.getChannel().writeAndFlush(
				CommandDefine.generateCommand(
					CommandDefine.CLIENT_LIST_RESPONSE,
					ImpalerConstant.CLIENT_ID_NONE,
					json
				)
			);
		} catch (JsonProcessingException e) {
			log.error("generate client list json error");
		}
	}

	@CommandMethod(type = defaultCommandType)
	public void handleDefaultCommand(Command command, Session session) {
		if(command.getTarget() == ImpalerConstant.CLIENT_ID_NONE) {
			command.setTarget(session.getClient().getClientId());
			for (Map.Entry<Integer, Client> entry : clientManager.getClientMap().entrySet()) {
				Client targetClient = entry.getValue();
				if(targetClient.getClientId() == session.getClient().getClientId()) {
					continue;
				}
				if(targetClient.containsSession(session.getType())) {
					targetClient.getSession(session.getType())
						.getChannel()
						.writeAndFlush(command);
				}
			}
		} else {
			if(clientManager.containsClient(command.getTarget())) {
				Session targetSession = clientManager.getClient(command.getTarget()).getSession(session.getType());
				if(!ObjectUtils.isEmpty(targetSession)) {
					command.setTarget(session.getClient().getClientId());
					targetSession.getChannel().writeAndFlush(command);
				}
			}
		}
		session.getChannel().writeAndFlush(CommandDefine.COMMAND_OK);
	}

}
