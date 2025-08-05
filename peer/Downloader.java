package peer;

import common.PeerInfo;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class Downloader {

    public static void downloadChunk(String fileName, PeerInfo peer, int chunkIndex) {
        try (Socket socket = new Socket(peer.getIp(), peer.getPort());
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject(fileName);
            out.writeObject(chunkIndex);

            byte[] chunkData = (byte[]) in.readObject();

            Path fileDir = Paths.get("shared", fileName);
            if (!Files.exists(fileDir)) Files.createDirectories(fileDir);

            Path chunkPath = fileDir.resolve("chunk_" + chunkIndex);
            Files.write(chunkPath, chunkData);

            System.out.println("[Downloader] Downloaded chunk " + chunkIndex + " from " + peer.getIp());

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void downloadFile(String fileName, List<PeerInfo> peers, int totalChunks) {
        Set<Integer> downloadedChunks = new HashSet<>();

        while (downloadedChunks.size() < totalChunks) {
            for (PeerInfo peer : peers) {
                for (int chunkIndex : peer.getChunkIndices()) {
                    if (!downloadedChunks.contains(chunkIndex)) {
                        downloadChunk(fileName, peer, chunkIndex);
                        downloadedChunks.add(chunkIndex);
                    }
                }
            }
        }

        System.out.println("[Downloader] File download complete.");
    }
}
