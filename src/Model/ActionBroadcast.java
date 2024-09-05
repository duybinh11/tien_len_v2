package Model;

import java.io.Serializable;

public class ActionBroadcast<T> implements Serializable {

    private int code;
    private T data;
    private int idRoom;

    public ActionBroadcast(int code) {
        this.code = code;
    }

    public ActionBroadcast(int code, int idRoom) {
        this.code = code;
        this.idRoom = idRoom;
    }

    public ActionBroadcast(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public ActionBroadcast(int code, T data, int idRoom) {
        this.code = code;
        this.data = data;
        this.idRoom = idRoom;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(int idRoom) {
        this.idRoom = idRoom;
    }

    @Override
    public String toString() {
        return "ActionBroadcast [code=" + code + ", data=" + data + ", idRoom=" + idRoom + "]";
    }

}
