package site.lankui.impaler.command;


import site.lankui.impaler.bean.Session;
import site.lankui.impaler.command.handler.CommandHandler;
import site.lankui.impaler.util.SpringBeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CommandInvoker {

	private Command command;
	private CommandFactory methodFactory;

	public CommandInvoker(Command command) {
		this.command = command;
		methodFactory = SpringBeanUtils.getBean(CommandFactory.class);
	}

	public void invoker(Session session) throws InvocationTargetException, IllegalAccessException {
		Method method = methodFactory.getCommandHandleMethod(command.getType());
		CommandHandler handler = new CommandHandler();
		method.invoke(handler, command, session);
	}

}
