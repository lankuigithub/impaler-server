package site.lankui.impaler.client.bean;

import io.netty.channel.Channel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import site.lankui.impaler.constant.SessionType;

@Setter
@Getter
@Builder
public class Session {
	private String sessionId;

	private SessionType type;

	private String ipAddress;

	private int port;

	private Channel channel;

	private Client client;
}
