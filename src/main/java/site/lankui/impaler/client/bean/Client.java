package site.lankui.impaler.client.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import site.lankui.impaler.command.Command;
import site.lankui.impaler.command.CommandDefine;
import site.lankui.impaler.constant.ImpalerConstant;
import site.lankui.impaler.constant.SessionType;
import site.lankui.impaler.constant.StatusType;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

@Builder
public class Client {
	@Setter
	@Getter
	private int clientId;
	@Setter
	@Getter
	private String name;
	@Setter
	@Getter
	private StatusType status;
	@Setter
	@JsonIgnore
	private Map<SessionType, Session> sessionMap;
	@Setter
	@JsonIgnore
	private ConcurrentLinkedQueue<Command> commandQueue;

	public void addSession(Session session) {
		synchronized (sessionMap) {
			sessionMap.put(session.getType(), session);
			session.setClient(this);
			status = StatusType.ON;
		}
	}

	public void removeSession(Session session) {
		synchronized (sessionMap) {
			sessionMap.remove(session.getType());
			if (sessionMap.isEmpty()) {
				status = StatusType.OFF;
			}
		}
	}

	public Session getSession(SessionType sessionType) {
		synchronized (sessionMap) {
			return sessionMap.get(sessionType);
		}
	}

	public void addCommand(Command command) {
		synchronized (commandQueue) {
			if (command.getTarget() == ImpalerConstant.CLIENT_ID_NONE) {
				return;
			}
			switch (command.getType()) {
				case CommandDefine.COMMAND_HEART_BEAT:
				case CommandDefine.COMMAND_REGISTER:
				case CommandDefine.COMMAND_CLIENT_LIST:
					break;
				default:
					commandQueue.add(command);
			}
		}
	}

	public void sendHistoryCommand(Session session) {
		synchronized (commandQueue) {
			Iterator<Command> iterator = commandQueue.iterator();
			while (iterator.hasNext()) {
				Command command = iterator.next();
				if(command.getSessionType() != session.getType()) {
					return;
				}
				iterator.remove();
				session.sendCommand(command);
			}
		}
	}
}
