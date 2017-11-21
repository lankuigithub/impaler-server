package site.lankui.impaler.client;

import io.netty.channel.Channel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class Client {

	private String clientId;

	private String ipAddress;

	private int port;

	private Channel channel;


}
