package site.lankui.impaler.command;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import site.lankui.impaler.constant.ImpalerConstant;

public class CommandDefine {

	public static final int COMMAND_HEART_BEAT = 0x00000001;
	public static final int COMMAND_MESSAGE = 0x00000002;
	public static final int COMMAND_IMAGE = 0x00000003;
	public static final int COMMAND_SCREEN = 0x00000004;
	public static final int COMMAND_CAMERA = 0x00000005;
	public static final int COMMAND_REGISTER = 0x00000006;
	public static final int COMMAND_CLIENT_LIST = 0x00000007;

	public static final String SPLIT_WORD = "IMPALER";

	public static final Command COMMAND_HEART_BEAT_CONSTANT;
	public static final Command COMMAND_REGISTER_CONSTANT;
	static {
		COMMAND_HEART_BEAT_CONSTANT = Command.builder()
			.type(COMMAND_HEART_BEAT)
			.target(ImpalerConstant.CLIENT_ID_NONE)
			.data(new byte[0])
			.build();
		COMMAND_REGISTER_CONSTANT = Command.builder()
			.type(COMMAND_REGISTER)
			.target(ImpalerConstant.CLIENT_ID_NONE)
			.data(new byte[0])
			.build();
	}

	public static Command generateCommand(int commandType, int target, String commandData) {
		byte[] data = commandData.getBytes(CharsetUtil.UTF_8);
		return Command.builder()
			.type(commandType)
			.target(target)
			.dataLength(data.length)
			.data(data)
			.build();
	}

	public static Command generateCommand(int commandType, int target, int commandData) {
		ByteBuf byteBuf = Unpooled.copyInt(commandData);
		byte[] data = new byte[byteBuf.readableBytes()];
		byteBuf.readBytes(data);
		return Command.builder()
			.type(commandType)
			.target(target)
			.dataLength(data.length)
			.data(data)
			.build();
	}
}
