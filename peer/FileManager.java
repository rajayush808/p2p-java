package peer;

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;

public class FileManager {
    private static final int CHUNK_SIZE = 1024 * 1024; // 1 MB

    public static List<Integer> splitFile(String filePath, String fileName) throws IOException {
        List<Integer> chunkIndices = new ArrayList<>();
        Path inputPath = Paths.get(filePath);
        Path outputDir = Paths.get("shared", fileName);
        Files.createDirectories(outputDir);

        byte[] buffer = new byte[CHUNK_SIZE];
        int index = 0;

        try (InputStream in = Files.newInputStream(inputPath)) {
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                Path chunkPath = outputDir.resolve("chunk_" + index);
                Files.write(chunkPath, Arrays.copyOf(buffer, bytesRead));
                System.out.println("[FileManager] Wrote chunk " + index);
                chunkIndices.add(index);
                index++;
            }
        }

        return chunkIndices;
    }
}
