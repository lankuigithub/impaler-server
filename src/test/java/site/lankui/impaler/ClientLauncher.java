package site.lankui.impaler;

public class ClientLauncher {

    public static void main(String[] args) throws Exception {
        Client client = new Client("127.0.0.1", 9000);
        client.run();
    }

}
