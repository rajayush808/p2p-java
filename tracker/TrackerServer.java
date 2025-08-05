package tracker;

import java.io.*;
import java.net.*;
import java.util.*;
import common.PeerInfo;

public class TrackerServer {
    private static final int PORT = 8888;
    private static final Map<String, List<PeerInfo>> fileRegistry = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[Tracker] Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

            String command = (String) in.readObject();

            if ("REGISTER".equalsIgnoreCase(command)) {
                String fileName = (String) in.readObject();
                PeerInfo peerInfo = (PeerInfo) in.readObject();

                fileRegistry.putIfAbsent(fileName, new ArrayList<>());
                fileRegistry.get(fileName).add(peerInfo);

                System.out.println("[Tracker] Registered: " + peerInfo + " for file: " + fileName);
                out.writeObject("REGISTERED");

            } else if ("QUERY".equalsIgnoreCase(command)) {
                String fileName = (String) in.readObject();
                List<PeerInfo> peers = fileRegistry.getOrDefault(fileName, new ArrayList<>());

                out.writeObject(peers);
                System.out.println("[Tracker] Sent peer list for file: " + fileName);
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
