package DTO;

import java.io.Serializable;

public class RoomDTO implements Serializable {

    private int id;
    private String name;
    private int numberOfClients;

    public RoomDTO(int id, String name, int numberOfClients) {
        this.id = id;
        this.name = name;
        this.numberOfClients = numberOfClients;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfClients() {
        return numberOfClients;
    }

    public void setNumberOfClients(int numberOfClients) {
        this.numberOfClients = numberOfClients;
    }

    @Override
    public String toString() {
        return "RoomDTO [id=" + id + ", name=" + name + ", numberOfClients=" + numberOfClients + "]";
    }
}
