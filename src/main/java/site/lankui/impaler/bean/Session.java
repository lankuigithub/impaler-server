package site.lankui.impaler.bean;

import io.netty.channel.Channel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class Session {
	private String sessionId;

	private String ipAddress;

	private int port;

	private Channel channel;

	private Client client;
}
