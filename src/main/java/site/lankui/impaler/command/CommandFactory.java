package site.lankui.impaler.command;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import site.lankui.impaler.command.handler.CommandHandler;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CommandFactory {

	private static List<Class> clazzList = new ArrayList<>();
	private Map<Integer, Method> commandHandleMethodMap = new HashMap<>();

	static {
		clazzList.add(CommandHandler.class);
	}

	@PostConstruct
	private void scanCommandHandler() {
		log.info("scan the command handler");
		for (Class clazz: clazzList) {
			for (Method method : clazz.getDeclaredMethods()) {
				CommandMethod commandMethod = method.getAnnotation(CommandMethod.class);
				if (ObjectUtils.isEmpty(commandMethod)) {
					continue;
				}
				for (int commandType: commandMethod.type()) {
					commandHandleMethodMap.put(commandType, method);
				}
			}
		}

	}

	public Method getCommandHandleMethod(int commandType) {
		Method method = commandHandleMethodMap.get(commandType);
		if(ObjectUtils.isEmpty(method)) {
			method = commandHandleMethodMap.get(CommandHandler.defaultCommandType);
		}
		return method;
	}
}
