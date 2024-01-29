import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket server;

    public Server(ServerSocket server) {
        this.server = server;
    }

    public void startServer() {
        try {
            while (!server.isClosed()) {
                Socket socket = server.accept();
                System.out.println("new client connected");
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            Server server = new Server(serverSocket);
            server.startServer();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
