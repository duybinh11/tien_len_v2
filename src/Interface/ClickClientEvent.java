package Interface;

import java.util.List;

import DTO.RoomDTO;
import Model.Card;

public interface ClickClientEvent {
    public void clickCreateRoom(String name);

    public void clickExit();

    public void doubleClickJoinRoom(RoomDTO roomDTO);

    public void clickStart();

    public void clickDanh(List<Card> cards);

    public void clickSkip();

}
