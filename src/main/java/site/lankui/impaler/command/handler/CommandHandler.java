package site.lankui.impaler.command.handler;


import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import site.lankui.impaler.bean.Client;
import site.lankui.impaler.bean.ClientManager;
import site.lankui.impaler.bean.Message;
import site.lankui.impaler.bean.Session;
import site.lankui.impaler.command.Command;
import site.lankui.impaler.command.CommandDefine;
import site.lankui.impaler.command.CommandMethod;
import site.lankui.impaler.constant.ImpalerConstant;
import site.lankui.impaler.constant.StatusType;
import site.lankui.impaler.util.Generator;
import site.lankui.impaler.util.JsonUtils;
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

	@CommandMethod(type = CommandDefine.COMMAND_HEART_BEAT)
	public void handleHeartbeat(Command command, Session session) {
		sendToMyself(CommandDefine.COMMAND_HEART_BEAT_CONSTANT, session);
	}

	@CommandMethod(type = CommandDefine.COMMAND_REGISTER)
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
		Map<String, Object> data = new HashMap<>();
		data.put("id", clientId);
		sendToMyself(
			CommandDefine.generateCommand(
				CommandDefine.COMMAND_REGISTER,
				ImpalerConstant.CLIENT_ID_NONE,
				JsonUtils.objectToString(data)
			),
			session
		);
	}

	@CommandMethod(type = CommandDefine.COMMAND_CLIENT_LIST)
	public void handleClientList(Command command, Session session) {
		List<Client> clientList = new ArrayList<>();
		for(Map.Entry<Integer, Client> entry: clientManager.getClientMap().entrySet()) {
			clientList.add(entry.getValue());
		}
		Map<String, Object> data = new HashMap<>();
		data.put("name", "客户端列表");
		data.put("data", clientList);
		Message message = Message.successMessage(data);
		sendToMyself(
			CommandDefine.generateCommand(
				CommandDefine.COMMAND_CLIENT_LIST,
				ImpalerConstant.CLIENT_ID_NONE,
				JsonUtils.objectToString(message)
			),
			session
		);
	}

	@CommandMethod(type = {CommandDefine.COMMAND_MESSAGE})
	public void handleMessage(Command command, Session session) {
		if(command.getTarget() == ImpalerConstant.CLIENT_ID_NONE) {
			sendToOthers(command, session);
		} else {
			sendToTarget(command, session);
		}
	}

	@CommandMethod(type = {CommandDefine.COMMAND_IMAGE})
	public void handleImage(Command command, Session session) {
		if(command.getTarget() == ImpalerConstant.CLIENT_ID_NONE) {
			sendToOthers(command, session);
		} else {
			sendToTarget(command, session);
		}
	}

	@CommandMethod(type = {CommandDefine.COMMAND_SCREEN, CommandDefine.COMMAND_CAMERA})
	public void handleCamera(Command command, Session session) {
		if(command.getTarget() == ImpalerConstant.CLIENT_ID_NONE) {
			sendToOthers(command, session);
		} else {
			sendToTarget(command, session);
		}
	}

	@CommandMethod(type = defaultCommandType)
	public void handleDefaultCommand(Command command, Session session) {
		log.error("unhandled command: type=" + command.getType() + " target=" + command.getTarget());
		if(command.getTarget() == ImpalerConstant.CLIENT_ID_NONE) {
			sendToOthers(command, session);
		} else {
			sendToTarget(command, session);
		}
	}

	private void sendToMyself(Command command, Session session) {
		session.getChannel().writeAndFlush(command);
	}

	private void sendToTarget(Command command, Session session) {
		Client client = clientManager.getClient(command.getTarget());
		if (ObjectUtils.isEmpty(client)) {
			return;
		}
		Session targetSession = client.getSession(session.getType());
		if (ObjectUtils.isEmpty(targetSession)) {
			return;
		}
		command.setTarget(session.getClient().getClientId());
		targetSession.getChannel().writeAndFlush(command);
	}

	private void sendToOthers(Command command, Session session) {
		Map<Integer, Client> clientMap = clientManager.getClientMap();
		for (Map.Entry<Integer, Client> entry : clientMap.entrySet()) {
			if (entry.getKey() == session.getClient().getClientId()) {
				continue;
			}
			Session targetSession = entry.getValue().getSession(session.getType());
			if (!ObjectUtils.isEmpty(targetSession)) {
				command.setTarget(session.getClient().getClientId());
				targetSession.getChannel().writeAndFlush(command);
			}
		}
	}
}
