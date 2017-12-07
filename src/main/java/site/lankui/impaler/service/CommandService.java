package site.lankui.impaler.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import site.lankui.impaler.client.bean.Session;
import site.lankui.impaler.command.Command;
import site.lankui.impaler.command.CommandDefine;
import site.lankui.impaler.command.CommandInvoker;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
@Scope(value = BeanDefinition.SCOPE_SINGLETON)
public class CommandService {

	private static final int MAX_THREAD_NUM = 5;
	private ExecutorService executorService;
	private static final Set<Integer> NO_REGISTER_SET = new HashSet<>();
	static {
		NO_REGISTER_SET.add(CommandDefine.COMMAND_HEART_BEAT);
		NO_REGISTER_SET.add(CommandDefine.COMMAND_REGISTER);
	}

	@PostConstruct
	public void init() {
		executorService = Executors.newFixedThreadPool(MAX_THREAD_NUM);
	}

	public void execute(Command command, Session session) {
		executorService.execute(() -> {
			if(!NO_REGISTER_SET.contains(command.getType())) {
				if(!isRegister(session)) {
					return;
				}
			}
			try {
				CommandInvoker invoker = new CommandInvoker(command);
				invoker.invoker(session);
			} catch (InvocationTargetException | IllegalAccessException e) {
				e.printStackTrace();
			}
		});
	}

	private boolean isRegister(Session session) {
		return !ObjectUtils.isEmpty(session.getClient());
	}
}
