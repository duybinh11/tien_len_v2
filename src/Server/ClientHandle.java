package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import DTO.RoomDTO;

import java.io.Serializable;
import Model.ActionBroadcast;
import Model.ClientData;
import Model.Room;

public class ClientHandle extends Thread implements Serializable {
    private Socket socket;
    private ObjectOutputStream writeObject;
    private ObjectInputStream readObject;
    private Integer postionRoom = null;
    private boolean isSkip = false;
    private boolean isStart = false;

    public ClientHandle(Socket socket) throws IOException {
        this.socket = socket;

        readObject = new ObjectInputStream(socket.getInputStream());
        writeObject = new ObjectOutputStream(socket.getOutputStream());

        broadcastListRoom();
    }

    @Override
    public void run() {
        while (true) {
            try {
                ActionBroadcast actionBroadcast = (ActionBroadcast) readObject.readObject();
                System.out.println(actionBroadcast);
                // + 2 -> tạo phòng
                if (actionBroadcast.getCode() == 2) {
                    String name = (String) actionBroadcast.getData();
                    Room room = Server.createRoom(this, name);
                    RoomDTO RoomDTO = room.toRoomDTO();
                    ActionBroadcast<RoomDTO> actionNewRoom = new ActionBroadcast<RoomDTO>(3, RoomDTO);
                    Server.broadCastAllClients(this, actionNewRoom);

                    ClientData clientData = getClientData();
                    Map<String, Object> mapData = Map.of(
                            "roomDTO", RoomDTO,
                            "clientData", clientData);
                    ActionBroadcast<Map<String, Object>> actionEnterRoom = new ActionBroadcast<Map<String, Object>>(4,
                            mapData);

                    broadcastThisClient(actionEnterRoom);
                }
                // 5 -> yêu cầu tham gia vào phòng
                else if (actionBroadcast.getCode() == 5) {
                    int idRoom = actionBroadcast.getIdRoom();
                    Room room = Server.getRoomById(idRoom);
                    System.out.println(socket);
                    room.addClient(this);

                    List<ClientData> clientDataOther = room.getListClient().stream().map(handle -> {
                        return handle.getClientData();
                    }).toList();

                    Map<String, Object> mapData = Map.of(
                            "roomDTO", room.toRoomDTO(),
                            "clientDatas", clientDataOther);

                    ActionBroadcast<Map<String, Object>> actionOAllInClient = new ActionBroadcast<Map<String, Object>>(
                            6,
                            mapData);
                    broadcastClientAllinRoom(room.getListClient(), actionOAllInClient);

                } else if (actionBroadcast.getCode() == 7) {
                    int idRoom = actionBroadcast.getIdRoom();
                    Room room = Server.getRoomById(idRoom);
                    room.getListClient().forEach(client -> {
                        if (client.equals(this)) {
                            client.setStart(true);
                        }
                    });
                    boolean isAllStart = room.getListClient().stream().allMatch(client -> client.isStart);
                    if (isAllStart) {
                        System.out.println("tat ca da san san");
                        
                    } else {
                        System.out.println("not tat ca da san san");
                    }
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcastListRoom() throws IOException {
        List<RoomDTO> roomsDto = Server.rooms.stream().map(room -> {
            return room.toRoomDTO();
        }).toList();
        ActionBroadcast<List<RoomDTO>> actionSendRooms = new ActionBroadcast<List<RoomDTO>>(1, roomsDto);
        writeObject.writeObject(actionSendRooms);
    }

    private void broadcastThisClient(@SuppressWarnings("rawtypes") ActionBroadcast actionBroadcast) throws IOException {
        System.out.println(actionBroadcast);
        writeObject.writeObject(actionBroadcast);
    }

    private void broadcastClientInRoom(List<ClientHandle> clientHandles,
            @SuppressWarnings("rawtypes") ActionBroadcast actionBroadcast) {
        clientHandles.forEach(client -> {
            if (!client.equals(this)) {
                try {
                    client.getWriteObject().writeObject(actionBroadcast);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void broadcastClientAllinRoom(List<ClientHandle> clientHandles,
            @SuppressWarnings("rawtypes") ActionBroadcast actionBroadcast) {
        clientHandles.forEach(client -> {
            try {
                client.getWriteObject().writeObject(actionBroadcast);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public ClientData getClientData() {
        return new ClientData(socket.getLocalAddress().toString(), socket.getPort(), postionRoom);
    }

    public Integer getPostionRoom() {
        return postionRoom;
    }

    public void setPostionRoom(Integer postionRoom) {
        this.postionRoom = postionRoom;
    }

    public boolean isSkip() {
        return isSkip;
    }

    public void setSkip(boolean isSkip) {
        this.isSkip = isSkip;
    }

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean isStart) {
        this.isStart = isStart;
    }

    @Override
    public String toString() {
        return "ClientHandle [socket=" + socket + "]";
    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectOutputStream getWriteObject() {
        return writeObject;
    }

    public ObjectInputStream getReadObject() {
        return readObject;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((socket == null) ? 0 : socket.hashCode());
        result = prime * result + ((writeObject == null) ? 0 : writeObject.hashCode());
        result = prime * result + ((readObject == null) ? 0 : readObject.hashCode());
        result = prime * result + ((postionRoom == null) ? 0 : postionRoom.hashCode());
        result = prime * result + (isSkip ? 1231 : 1237);
        result = prime * result + (isStart ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClientHandle other = (ClientHandle) obj;
        if (socket == null) {
            if (other.socket != null)
                return false;
        } else if (!socket.equals(other.socket))
            return false;
        if (writeObject == null) {
            if (other.writeObject != null)
                return false;
        } else if (!writeObject.equals(other.writeObject))
            return false;
        if (readObject == null) {
            if (other.readObject != null)
                return false;
        } else if (!readObject.equals(other.readObject))
            return false;
        if (postionRoom == null) {
            if (other.postionRoom != null)
                return false;
        } else if (!postionRoom.equals(other.postionRoom))
            return false;
        if (isSkip != other.isSkip)
            return false;
        if (isStart != other.isStart)
            return false;
        return true;
    }

}
