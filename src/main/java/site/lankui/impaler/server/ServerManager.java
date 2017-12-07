package site.lankui.impaler.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.lankui.impaler.client.ConnectManager;
import site.lankui.impaler.client.bean.Client;
import site.lankui.impaler.client.bean.Session;
import site.lankui.impaler.command.Command;
import site.lankui.impaler.command.CommandDefine;
import site.lankui.impaler.command.Message;
import site.lankui.impaler.constant.ImpalerConstant;
import site.lankui.impaler.constant.SessionType;
import site.lankui.impaler.util.JsonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServerManager {

	@Autowired
	private ConnectManager connectManager;

	public void pushClientList() {
		List<Client> clientList = new ArrayList<>();
		for(Map.Entry<Integer, Client> entry: connectManager.getClientMap().entrySet()) {
			clientList.add(entry.getValue());
		}
		Map<String, Object> data = new HashMap<>();
		data.put("name", "客户端列表");
		data.put("list", clientList);
		Message message = Message.successMessage(data);
		Command command = CommandDefine.generateCommand(
			CommandDefine.COMMAND_CLIENT_LIST,
			ImpalerConstant.CLIENT_ID_NONE,
			JsonUtils.objectToString(message)
		);
		for(Map.Entry<Integer, Client> clientEntry: connectManager.getClientMap().entrySet()) {
			for (Map.Entry<SessionType, Session> sessionEntry: clientEntry.getValue().getSessionMap().entrySet()) {
				sessionEntry.getValue().getChannel().writeAndFlush(command);
				break;
			}
		}
	}
}
