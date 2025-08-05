// peer/Uploader.java

package peer;

import java.io.*;
import java.net.*;
import java.nio.file.*;

public class Uploader {
    private static final int PORT = 9000; // Make sure this matches PeerInfo's port

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[Uploader] Listening on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            String fileName = (String) in.readObject();
            int chunkIndex = (Integer) in.readObject();

            String chunkPath = "shared/" + fileName + "/chunk_" + chunkIndex;
            byte[] chunkData = Files.readAllBytes(Paths.get(chunkPath));

            out.writeObject(chunkData);
            System.out.println("[Uploader] Sent chunk " + chunkIndex + " of file " + fileName);

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Uploader] Failed to send chunk: " + e.getMessage());
        }
    }
}
