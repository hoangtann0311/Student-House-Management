
package Model;

public class Room {
    public int RoomID;
    public String RoomNumber;
    public int Capacity;
    public int MonthlyRent;
    public String Status;
    private int currentOccupants;

    public Room() {
    }

    public Room(int RoomID, String RoomNumber, int Capacity, int MonthlyRent, String Status, int currentOccupants) {
        this.RoomID = RoomID;
        this.RoomNumber = RoomNumber;
        this.Capacity = Capacity;
        this.MonthlyRent = MonthlyRent;
        this.Status = Status;
        this.currentOccupants = currentOccupants;
    }

    public int getRoomID() {
        return RoomID;
    }

    public void setRoomID(int RoomID) {
        this.RoomID = RoomID;
    }

    public String getRoomNumber() {
        return RoomNumber;
    }

    public void setRoomNumber(String RoomNumber) {
        this.RoomNumber = RoomNumber;
    }

    public int getCapacity() {
        return Capacity;
    }

    public void setCapacity(int Capacity) {
        this.Capacity = Capacity;
    }

    public int getMonthlyRent() {
        return MonthlyRent;
    }

    public void setMonthlyRent(int MonthlyRent) {
        this.MonthlyRent = MonthlyRent;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public int getCurrentOccupants() {
        return currentOccupants;
    }

    public void setCurrentOccupants(int currentOccupants) {
        this.currentOccupants = currentOccupants;
    }

   

    

    
    
    
}
