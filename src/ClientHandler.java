import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    private static ArrayList<ClientHandler> clientHandlerArrayList = new ArrayList<>();
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String clientName;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Enter your name :");
            clientName = reader.readLine();
            clientHandlerArrayList.add(this);
            broadcastMessage("SERVER : " + clientName + " has join the chat.");
        } catch (IOException ex) {
            terminateConnection(socket, reader, writer);
        }
    }

    @Override
    public void run() {
        String clientMessage;
        while (socket.isConnected()) {
            try {
                clientMessage = reader.readLine();
                broadcastMessage(clientMessage);
            } catch (IOException e) {
                terminateConnection(socket, reader, writer);
                break;
            }
        }
    }

    private void terminateConnection(Socket socket2, BufferedReader reader2, BufferedWriter writer2) {
        removeClientHandler();
        try {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcastMessage(String clientMessage) {
        for (ClientHandler clientHandler : clientHandlerArrayList) {
            try {
                if (!clientHandler.clientName.equals(clientName)) {
                    clientHandler.writer.write(clientMessage);
                    clientHandler.writer.newLine();
                    clientHandler.writer.flush();
                }
            } catch (IOException ex) {
                terminateConnection(socket, reader, writer);
            }
        }
    }

    public void removeClientHandler() {
        clientHandlerArrayList.remove(this);
        broadcastMessage("SERVER : " + clientName + " has left the chat");
    }
}
