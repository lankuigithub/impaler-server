package site.lankui.impaler.command;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CommandFactory {
	private Map<Integer, Method> commandHandleMethodMap = new HashMap<>();

	@PostConstruct
	private void scanCommandHandler() {
		log.info("scan the command handler");
		for (Method method : CommandHandler.class.getDeclaredMethods()) {
			CommandMethod commandMethod = method.getAnnotation(CommandMethod.class);
			if(!ObjectUtils.isEmpty(commandMethod)){
				commandHandleMethodMap.put(commandMethod.type(), method);
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
