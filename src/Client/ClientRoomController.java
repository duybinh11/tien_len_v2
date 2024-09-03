package Client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import DTO.RoomDTO;
import Interface.ClickClientEvent;
import Model.ClientData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ClientRoomController {

    @FXML
    private Label txtPort1;

    @FXML
    private Label txtPort2;

    @FXML
    private Label txtPort3;

    @FXML
    private Label txtPort4;

    @FXML
    private Label txtPosition1;

    @FXML
    private Label txtPosition2;

    @FXML
    private Label txtPosition3;

    @FXML
    private Label txtPosition4;
    @FXML
    private Button btnStart;

    private ClickClientEvent clickClientEvent;

    public void setClickClientEvent(ClickClientEvent clickClientEvent) {
        this.clickClientEvent = clickClientEvent;
    }

    @FXML
    void btnStart(ActionEvent event) {
        clickClientEvent.clickStart();
        btnStart.setText("Đã sẵn sàng");
    }

    public void updateClientInRoom(ClientData c, List<ClientData> clientDatas) {
        setPostion1(c);

        int index = c.getPostion();

        for (int i = 0; i < 3; i++) {
            if (index + 1 > 4) {
                index = 1;
            } else {
                index++;
            }
            int currentIndex = index;
            ClientData clientData = clientDatas.stream().filter(t -> t.getPostion() == currentIndex).findFirst()
                    .orElse(null);
            if (clientData != null) {
                if (i == 0) {
                    setPostion2(clientData);
                } else if (i == 1) {
                    setPostion3(clientData);
                } else if (i == 2) {
                    setPostion4(clientData);
                }
            }

        }
    }

    public void updateAfterCreateRoom(ClientData clientData) {
        setPostion1(clientData);
    }

    public void setPostion1(ClientData c) {
        txtPosition1.setText("" + c.getPostion());
        txtPort1.setText("" + c.getPort());
    }

    public void setPostion2(ClientData c) {
        txtPosition2.setText("" + c.getPostion());
        txtPort2.setText("" + c.getPort());
    }

    public void setPostion3(ClientData c) {
        txtPosition3.setText("" + c.getPostion());
        txtPort3.setText("" + c.getPort());
    }

    public void setPostion4(ClientData c) {
        txtPosition4.setText("" + c.getPostion());
        txtPort4.setText("" + c.getPort());
    }

    void visibleBtnStart() {
        btnStart.setVisible(true);
        btnStart.setManaged(true);
    }

    void inVisibleBtnStart() {
        btnStart.setVisible(false);
        btnStart.setManaged(false);
    }
}
