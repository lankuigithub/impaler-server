package site.lankui.impaler.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import site.lankui.impaler.MainLauncher;

@Configuration
@ComponentScan(basePackageClasses = {MainLauncher.class})
@PropertySource(value = {"classpath:application.properties"})
public class SpringConfig {
}
