package Interface;

import DTO.RoomDTO;

public interface ClickClientEvent {
    public void clickCreateRoom(String name);

    public void clickExit();

    public void doubleClickJoinRoom(RoomDTO roomDTO);

    public void clickStart();

}
