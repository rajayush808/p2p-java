// common/PeerInfo.java

package common;

import java.io.Serializable;
import java.util.List;

public class PeerInfo implements Serializable {
    private String ip;
    private int port;
    private List<Integer> chunkIndices;

    public PeerInfo(String ip, int port, List<Integer> chunkIndices) {
        this.ip = ip;
        this.port = port;
        this.chunkIndices = chunkIndices;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public List<Integer> getChunkIndices() {
        return chunkIndices;
    }

    @Override
    public String toString() {
        return "PeerInfo{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", chunks=" + chunkIndices +
                '}';
    }
}
