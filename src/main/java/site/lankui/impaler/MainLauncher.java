package site.lankui.impaler;

import lombok.extern.slf4j.Slf4j;
import site.lankui.impaler.launcher.SpringLauncher;

@Slf4j
public class MainLauncher {
    public static void main(String[] args) throws InterruptedException {
    	log.info("Impaler Server starting");
        SpringLauncher springLauncher = new SpringLauncher();
        springLauncher.start();
		log.info("Impaler Server started");
    }
}