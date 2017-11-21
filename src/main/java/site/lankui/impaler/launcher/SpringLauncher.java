package site.lankui.impaler.launcher;


import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import site.lankui.impaler.config.SpringConfig;

public class SpringLauncher {
    public void start() {
        new AnnotationConfigApplicationContext(SpringConfig.class);
    }
}
