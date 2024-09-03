package Server;

import java.util.ArrayList;
import java.util.List;

import Model.Room;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class ServerController {

    private List<ClientHandle> listClients = new ArrayList<>();
    private List<Room> listRoom = new ArrayList<>();
    @FXML
    private TextArea txtClient;

    @FXML
    private TextArea txtRoom;

    public void updateListClient(ClientHandle clientHandle) {
        listClients.add(clientHandle);
        txtClient.setText(listClients.toString());
    }

    public void updateListRoom(Room room) {
        listRoom.add(room);
        txtRoom.setText(listRoom.toString());
    }

}