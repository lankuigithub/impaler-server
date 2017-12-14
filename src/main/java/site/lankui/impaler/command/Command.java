package site.lankui.impaler.command;

import lombok.*;
import site.lankui.impaler.constant.SessionType;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Command {
	@Setter
	@Getter
	private int type;
	@Setter
	@Getter
	private int target;
	@Setter
	@Getter
	private int dataLength;
	@Setter
	@Getter
	private byte[] data;
	@Setter
	@Getter
	private SessionType sessionType;
	@Setter
	@Getter
	private Date date;

	public String toString() {
		return "{type:" + type + ",target:" + target + ",dataLength:" + dataLength + "}";
	}
}
