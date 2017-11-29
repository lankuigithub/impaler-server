package site.lankui.impaler.command;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Command {
	@Setter
	@Getter
	private int type;
	@Setter
	@Getter
	private int dataLength;
	@Setter
	@Getter
	private byte[] data;
}
