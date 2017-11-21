package site.lankui.impaler;

import site.lankui.impaler.launcher.SpringLauncher;

public class MainLauncher {
    public static void main(String[] args) throws InterruptedException {
        SpringLauncher springLauncher = new SpringLauncher();
        springLauncher.start();
    }
}