package site.lankui.impaler.command;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import site.lankui.impaler.constant.ImpalerConstant;

public class CommandDefine {

	public static final int PING = 0x00000000;
	public static final int PONG = 0x00000001;
	public static final int MESSAGE = 0x00000002;
	public static final int IMAGE = 0x00000003;
	public static final int REGISTER_REQUEST = 0x10000001;
	public static final int REGISTER_RESPONSE = 0x10000002;
	public static final int CLIENT_LIST_REQUEST = 0x10000003;
	public static final int CLIENT_LIST_RESPONSE = 0x10000004;
	public static final int ERROR = 0x7FFFFFFE;
	public static final int OK = 0x7FFFFFFF;

	public static final String SPLIT_WORD = "IMPALER";


	public static final Command COMMAND_REGISTER;
	public static final Command COMMAND_PONG;
	public static final Command COMMAND_ERROR;
	public static final Command COMMAND_OK;
	static {
		COMMAND_REGISTER = Command.builder()
			.type(REGISTER_REQUEST)
			.target(ImpalerConstant.CLIENT_ID_NONE)
			.data(new byte[0])
			.build();
		COMMAND_PONG = Command.builder()
			.type(PONG)
			.target(ImpalerConstant.CLIENT_ID_NONE)
			.data(new byte[0])
			.build();
		COMMAND_ERROR = Command.builder()
			.type(ERROR)
			.target(ImpalerConstant.CLIENT_ID_NONE)
			.data(new byte[0])
			.build();
		COMMAND_OK = Command.builder()
			.type(OK)
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
