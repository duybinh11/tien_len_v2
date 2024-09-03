package Model;

import java.io.Serializable;

public class ClientData implements Serializable {
    private String host;
    private int port;
    private int postion;

    public ClientData(String host, int port, int postion) {
        this.host = host;
        this.port = port;
        this.postion = postion;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPostion() {
        return postion;
    }

    public void setPostion(int postion) {
        this.postion = postion;
    }

    @Override
    public String toString() {
        return "ClientData [host=" + host + ", port=" + port + ", postion=" + postion + "]";
    }

}