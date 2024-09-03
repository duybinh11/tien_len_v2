package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Interface.ClickClientEvent;
import Model.ActionBroadcast;
import Model.ClientData;
import Model.Room;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.*;

import DTO.RoomDTO;;

public class Client extends Application implements ClickClientEvent {
    private final int PORT = 12345;
    private final String HOST = "localhost";
    private ObjectOutputStream writeObject;
    private ObjectInputStream readObject;
    private List<RoomDTO> roomDTOs = new ArrayList<>();
    private ClientController controllerHome;
    private ClientRoomController controllerRoom;
    private Stage primaryStage;
    private Scene homeScene;
    private Socket socket;
    private ClientData client;
    private RoomDTO roomJoining;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewHome.fxml"));
        Parent root = loader.load();
        controllerHome = loader.getController();

        // Khởi tạo và lưu trữ Scene đầu tiên (homeScene)
        homeScene = new Scene(root, 800, 800);
        controllerHome.setInterfaceClick(this);

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(homeScene); // Sử dụng homeScene
        primaryStage.show();
        new Thread(() -> {
            try {
                socket = new Socket(HOST, PORT);
                writeObject = new ObjectOutputStream(socket.getOutputStream());
                readObject = new ObjectInputStream(socket.getInputStream());
                System.out.println("Connected to the game server.");

                while (true) {
                    try {
                        @SuppressWarnings("rawtypes")
                        ActionBroadcast actionBroadcast = (ActionBroadcast) readObject.readObject();
                        System.out.println(actionBroadcast);

                        // 1-> lấy danh sách tất cả các phòng
                        if (actionBroadcast.getCode() == 1) {
                            @SuppressWarnings("unchecked")
                            List<RoomDTO> rooms = (List<RoomDTO>) actionBroadcast.getData();
                            setRoomsUi(rooms);
                        }
                        // 3 -> có 1 phòng mới được tạo
                        else if (actionBroadcast.getCode() == 3) {
                            RoomDTO roomDTO = (RoomDTO) actionBroadcast.getData();
                            List<RoomDTO> dtos = Collections.singletonList(roomDTO);
                            setRoomsUi(dtos);
                        }
                        // 4 -> mời vào phòng
                        else if (actionBroadcast.getCode() == 4) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> mapData = (Map<String, Object>) actionBroadcast.getData();
                            roomJoining = (RoomDTO) mapData.get("roomDTO");
                            System.out.println(roomJoining);
                            ClientData clientData = (ClientData) mapData.get("clientData");
                            Platform.runLater(() -> {
                                nextView(() -> {
                                    updateAfterCreateRoom(clientData);
                                    controllerRoom.inVisibleBtnStart();
                                });
                            });
                        }
                        // +6 -> server gửi danh sách socket của các client khác ở trong phòng ddeer cap
                        // nhat ui
                        else if (actionBroadcast.getCode() == 6) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> mapData = (Map<String, Object>) actionBroadcast.getData();
                            roomJoining = (RoomDTO) mapData.get("roomDTO");
                            @SuppressWarnings("unchecked")
                            List<ClientData> clientDatas = (List<ClientData>) mapData.get("clientDatas");

                            if (clientDatas.size() > 1) {
                                Platform.runLater(() -> {
                                    controllerRoom.visibleBtnStart();
                                });
                            }

                            ClientData clientCurrent = findClientDataCurrent(clientDatas);
                            System.out.println(roomJoining);
                            Platform.runLater(() -> {
                                nextView(() -> {
                                    updateClientInRoom(clientCurrent, clientDatas);
                                });
                            });

                        }

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void clickCreateRoom(String name) {
        ActionBroadcast<String> actionCreateRoom = new ActionBroadcast<String>(2, name);
        try {
            writeObject.writeObject(actionCreateRoom);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRoomsUi(List<RoomDTO> newRooms) {
        this.roomDTOs.addAll(newRooms);
        Platform.runLater(() -> {
            controllerHome.updateRooms(roomDTOs);
        });
    }

    // ui room

    public void updateAfterCreateRoom(ClientData clientData) {
        if (controllerRoom != null) {
            controllerRoom.updateAfterCreateRoom(clientData);
        } else {
            System.out.println("controller null");
        }

    }

    public void updateClientInRoom(ClientData c, List<ClientData> clientDatas) {
        if (controllerRoom != null) {
            controllerRoom.updateClientInRoom(c, clientDatas);
        } else {
            System.out.println("controller null");
        }
    }

    public void nextView(Runnable onComplete) {
        try {
            FXMLLoader loaderRoom = new FXMLLoader(getClass().getResource("ViewRoom.fxml"));
            Parent rootRoom = loaderRoom.load();
            controllerRoom = loaderRoom.getController();
            controllerRoom.setClickClientEvent(this);

            Scene roomScene = new Scene(rootRoom);
            primaryStage.setScene(roomScene);
            // primaryStage.setFullScreen(true);
            primaryStage.show();

            if (onComplete != null) {
                onComplete.run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clickExit() {
        primaryStage.setScene(homeScene);
        primaryStage.setFullScreen(false);
    }

    @Override
    public void doubleClickJoinRoom(RoomDTO roomDTO) {
        if (roomDTO.getNumberOfClients() < 4) {
            ActionBroadcast actionJoinRoom = new ActionBroadcast<>(5, roomDTO.getId());
            System.out.println("gui yeu cau tham gia");
            try {
                writeObject.writeObject(actionJoinRoom);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("phong da đầy");
        }
    }

    private ClientData findClientDataCurrent(List<ClientData> clientDatas) {
        return clientDatas.stream().filter(client -> client.getPort() == socket.getLocalPort()).findFirst()
                .orElse(null);
    }

    @Override
    public void clickStart() {
        ActionBroadcast actionStart = new ActionBroadcast<>(7, roomJoining.getId());
        try {
            writeObject.writeObject(actionStart);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}