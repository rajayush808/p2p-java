// peer/Peer.java

package peer;

import common.PeerInfo;
import peer.FileManager;
import peer.Downloader;

import java.io.*;
import java.net.*;
import java.util.*;

public class Peer {
    private static final String TRACKER_HOST = "localhost";
    private static final int TRACKER_PORT = 8888;
    private static final int PEER_PORT = 9000; // Change this for each peer

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Prompt for file to share
            System.out.print("Enter full path to file to share: ");
            String filePath = scanner.nextLine();
            File file = new File(filePath);

            if (!file.exists()) {
                System.err.println("File does not exist.");
                return;
            }

            String fileName = file.getName();

            // Split file and get chunk indices
            List<Integer> chunkIndices = FileManager.splitFile(filePath, fileName);

            // Register with tracker
            try (Socket socket = new Socket(TRACKER_HOST, TRACKER_PORT);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                out.writeObject("REGISTER");
                out.writeObject(fileName);
                out.writeObject(new PeerInfo(InetAddress.getLocalHost().getHostAddress(), PEER_PORT, chunkIndices));

                String response = (String) in.readObject();
                System.out.println("[Peer] Tracker response: " + response);
            }

            // Prompt for file to download
            System.out.print("Enter file name to download: ");
            String queryFile = scanner.nextLine();

            List<PeerInfo> peers;
            try (Socket socket = new Socket(TRACKER_HOST, TRACKER_PORT);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                out.writeObject("QUERY");
                out.writeObject(queryFile);

                peers = (List<PeerInfo>) in.readObject();
                System.out.println("[Peer] Peers with file: " + peers);
            }

            if (!peers.isEmpty()) {
                System.out.print("Enter number of total chunks: ");
                int totalChunks = Integer.parseInt(scanner.nextLine());

                Downloader.downloadFile(queryFile, peers, totalChunks);
            } else {
                System.out.println("No peers found for this file.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
