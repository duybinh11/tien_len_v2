package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Interface.ClickClientEvent;
import Model.ActionBroadcast;
import Model.Card;
import Model.ClientData;
import Model.Room;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.*;

import DTO.RoomDTO;
import Enum.Rank;
import Enum.Suit;;

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
    private RoomDTO roomJoining;
    private List<Card> cardOfClientOther = new ArrayList<>();
    private List<Card> cardOfClientThis = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ViewHome.fxml"));
        Parent root = loader.load();
        controllerHome = loader.getController();

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
                        System.out.println("action client get : " + actionBroadcast);

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

                            ClientData clientCurrent = findClientDataCurrent(clientDatas);
                            System.out.println(roomJoining);
                            Platform.runLater(() -> {
                                nextView(() -> {

                                    updateClientInRoom(clientCurrent, clientDatas);
                                });
                            });

                        }
                        // +8 -> nhận bài để chơi
                        else if (actionBroadcast.getCode() == 8) {
                            List<Card> cards = (List<Card>) actionBroadcast.getData();
                            Collections.sort(cards, Comparator
                                    .comparing(Card::getRank)
                                    .thenComparing(Card::getSuit));
                            cardOfClientThis.addAll(cards);
                            System.out.println(cards);
                            Platform.runLater(() -> {
                                controllerRoom.displayCardImages(cards);
                                controllerRoom.inVisibleBtnStart();
                            });

                        }
                        // 9 den luot danh
                        else if (actionBroadcast.getCode() == 9) {
                            cardOfClientOther.clear();
                            Platform.runLater(() -> {
                                controllerRoom.visibleBtnDanh();
                                controllerRoom.visibleBtnSkip();
                            });
                        }
                        // 11 cho den luot
                        else if (actionBroadcast.getCode() == 11) {
                            Platform.runLater(() -> {
                                controllerRoom.inVisibleBtnDanh();
                                controllerRoom.inVisibleBtnSkip();
                            });
                        }
                        // +12 -> aciton server gui list card tu clien khac danh
                        else if (actionBroadcast.getCode() == 12) {
                            cardOfClientOther.clear();
                            List<Card> cards = (List<Card>) actionBroadcast.getData();
                            cardOfClientOther.addAll(cards);
                            System.out.println("card from server : " + cards);
                            Platform.runLater(() -> {
                                controllerRoom.displayCardAll(cards);
                            });
                        }
                        // +13 -> action gui aciton cho client khac tiep theo danh
                        else if (actionBroadcast.getCode() == 13) {
                            Platform.runLater(() -> {
                                controllerRoom.visibleBtnDanh();
                                controllerRoom.visibleBtnSkip();
                            });
                        }
                        // +15 -> reset card in room
                        else if (actionBroadcast.getCode() == 15) {
                            List<Card> cardTemp = new ArrayList<>();
                            Platform.runLater(() -> {
                                controllerRoom.displayCardAll(cardTemp);
                            });
                        }
                        // +17 -> reset game va chuan bi game moi
                        else if (actionBroadcast.getCode() == 17) {
                            List<Card> cardTemp = new ArrayList<>();
                            Platform.runLater(() -> {
                                controllerRoom.inVisibleBtnDanh();
                                controllerRoom.inVisibleBtnSkip();
                                controllerRoom.displayCardAll(cardTemp);
                                controllerRoom.displayCardImages(cardTemp);
                                controllerRoom.visibleBtnStart();
                                controllerRoom.setFalseIsStart();
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

    @Override
    public void clickDanh(List<Card> cards) {
        System.out.println("click danh");
        System.out.println(cardOfClientThis);
        System.out.println("card danh : " + cards);

        // Tạo một bản sao của danh sách cards
        List<Card> cardsCopy = new ArrayList<>(cards);
        System.out.println("card of clientOther :" + cardOfClientOther);

        // checkValidator(cardsCopy);
        System.out.println("bai danh co hop le : " + checkValid(cardsCopy));

        if (cardOfClientOther.isEmpty() && checkValid(cardsCopy)) {
            broadcastDanhBai(cardsCopy);
            cardOfClientThis.removeAll(cards);
            controllerRoom.resetCartSlect();
        } else if (checkValid(cardsCopy) && checkCompareCard(cardsCopy)) {
            broadcastDanhBai(cardsCopy);
            cardOfClientThis.removeAll(cards);
            controllerRoom.resetCartSlect();
        } else {
            Platform.runLater(() -> {
                controllerRoom.setMessageNotifilcation("bai danh kh hop le");
            });
        }
        System.out.println("bai hien tai : " + cardOfClientThis);

        if (cardOfClientThis.size() == 0) {
            ActionBroadcast actionEndGame = new ActionBroadcast<>(16, roomJoining.getId());
            try {
                writeObject.writeObject(actionEndGame);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private boolean checkValid(List<Card> cards) {
        if (cards.size() == 1 || isPair(cards) || isTriplet(cards) || isFourOfAKind(cards) || isFourOfAKind(cards)
                || isStraight(cards)) {
            return true;
        }
        return false;
    }

    public void broadcastDanhBai(List<Card> cardsCopy) {
        ActionBroadcast<List<Card>> cardsHand = new ActionBroadcast<List<Card>>(10, cardsCopy, roomJoining.getId());
        try {
            writeObject.writeObject(cardsHand);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            controllerRoom.displayCardImages(cardOfClientThis);
            controllerRoom.displayCardAll(cardsCopy);
            controllerRoom.setMessageNotifilcation("");
        });
    }

    public boolean checkCompareCard(List<Card> cards) {
        if (cardOfClientOther.size() != cards.size()) {
            return false;
        }

        if (cardOfClientOther.size() == 1) {
            return compareOneCardMoreThan(cards.get(0), cardOfClientOther.get(0));
        } else if (isPair(cardOfClientOther) && isPair(cards)) {
            return compareOneCardMoreThan(getCardMax(cards), getCardMax(cardOfClientOther));
        } else if (isTriplet(cardOfClientOther) && isPair(cards)) {
            return compareOneCardMoreThan(getCardMax(cards), getCardMax(cardOfClientOther));
        } else if (isStraight(cardOfClientOther) && isStraight(cards)) {
            return compareOneCardMoreThan(getCardMax(cards), getCardMax(cardOfClientOther));
        } else if (isFourOfAKind(cardOfClientOther) && isFourOfAKind(cards)) {
            return compareOneCardMoreThan(getCardMax(cards), getCardMax(cardOfClientOther));
        }

        return false;
    }

    public Card getCardMax(List<Card> cards) {
        return cards.stream().max((card1, card2) -> {
            int rankCompare = card1.getRank().ordinal() - card2.getRank().ordinal();
            if (rankCompare == 0) {
                return card1.getSuit().ordinal() - card2.getSuit().ordinal();
            } else {
                return rankCompare;
            }
        }).orElse(null);
    }

    private boolean compareOneCardMoreThan(Card card1, Card card2) {
        if (card1.getRank().ordinal() > card2.getRank().ordinal()) {
            return true;
        } else if (card1.getRank().ordinal() == card2.getRank().ordinal()) {
            if (card1.getSuit().ordinal() > card2.getSuit().ordinal()) {
                return true;
            }
        }
        return false;
    }

    public boolean isPair(List<Card> cards) {
        return cards.size() == 2 && cards.get(0).getRank() == cards.get(1).getRank();
    }

    public boolean isTriplet(List<Card> cards) {
        return cards.size() == 3 &&
                cards.get(0).getRank() == cards.get(1).getRank() &&
                cards.get(1).getRank() == cards.get(2).getRank();
    }

    public boolean isFourOfAKind(List<Card> cards) {
        return cards.size() == 4 &&
                cards.get(0).getRank() == cards.get(1).getRank() &&
                cards.get(1).getRank() == cards.get(2).getRank() &&
                cards.get(2).getRank() == cards.get(3).getRank();
    }

    public boolean isStraight(List<Card> cards) {
        if (cards.size() < 3) {
            return false;
        }

        cards.sort(Comparator.comparing(card -> card.getRank().ordinal()));

        for (int i = 0; i < cards.size() - 1; i++) {
            if (cards.get(i + 1).getRank().ordinal() != cards.get(i).getRank().ordinal() + 1) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void clickSkip() {
        ActionBroadcast actionSkip = new ActionBroadcast<>(14, roomJoining.getId());
        try {
            writeObject.writeObject(actionSkip);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}