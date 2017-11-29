package site.lankui.impaler.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.lankui.impaler.client.Client;
import site.lankui.impaler.client.ClientManager;
import site.lankui.impaler.command.Command;

import java.util.Map;

import static site.lankui.impaler.command.CommandDefine.*;

@Service
public class CommandService {

	private static final Command PONG_COMMAND = Command.builder().type(PONG).data(new byte[0]).build();
	private static final Command OK_COMMAND = Command.builder().type(OK).data(new byte[0]).build();

	@Autowired
	private ClientManager clientManager;

	public void execute(Command command, Client client) {
		switch (command.getType()) {
			case PING:
				client.getChannel().writeAndFlush(PONG_COMMAND);
				break;
			default:
				for (Map.Entry<String, Client> entry : clientManager.getClientMap().entrySet()) {
					if (!entry.getKey().equals(client.getClientId())) {
						entry.getValue().getChannel().writeAndFlush(command);
					}
				}
				client.getChannel().writeAndFlush(OK_COMMAND);
		}
	}

}
