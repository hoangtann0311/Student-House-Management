package Model;

public class MeterReading {

    private int readingId;
    private int roomId;
    private String roomNumber;
    private int readingMonth;
    private int readingYear;
    private int electricityIndex;
    private int waterIndex;

    public MeterReading() {
    }

    public MeterReading(int readingId, int roomId, String roomNumber, int readingMonth, int readingYear, int electricityIndex, int waterIndex) {
        this.readingId = readingId;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
        this.readingMonth = readingMonth;
        this.readingYear = readingYear;
        this.electricityIndex = electricityIndex;
        this.waterIndex = waterIndex;
    }

    public int getReadingId() {
        return readingId;
    }

    public void setReadingId(int readingId) {
        this.readingId = readingId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getReadingMonth() {
        return readingMonth;
    }

    public void setReadingMonth(int readingMonth) {
        this.readingMonth = readingMonth;
    }

    public int getReadingYear() {
        return readingYear;
    }

    public void setReadingYear(int readingYear) {
        this.readingYear = readingYear;
    }

    public int getElectricityIndex() {
        return electricityIndex;
    }

    public void setElectricityIndex(int electricityIndex) {
        this.electricityIndex = electricityIndex;
    }

    public int getWaterIndex() {
        return waterIndex;
    }

    public void setWaterIndex(int waterIndex) {
        this.waterIndex = waterIndex;
    }

}
