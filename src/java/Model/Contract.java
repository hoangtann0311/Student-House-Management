package Model;

import java.math.BigDecimal;
import java.util.Date;

public class Contract {

    private int contractID;
    private int roomID;
    private Date startDate;
    private Date endDate;
    private BigDecimal depositAmount;
    private String status;
    private String roomNumber;
    private String tenantName;
    private int tenantUserId;
    private double electricPrice, waterPrice, wifiPrice, garbagePrice;

    public double getElectricPrice() {
        return electricPrice;
    }

    public void setElectricPrice(double electricPrice) {
        this.electricPrice = electricPrice;
    }

    public double getWaterPrice() {
        return waterPrice;
    }

    public void setWaterPrice(double waterPrice) {
        this.waterPrice = waterPrice;
    }

    public double getWifiPrice() {
        return wifiPrice;
    }

    public void setWifiPrice(double wifiPrice) {
        this.wifiPrice = wifiPrice;
    }

    public double getGarbagePrice() {
        return garbagePrice;
    }

    public void setGarbagePrice(double garbagePrice) {
        this.garbagePrice = garbagePrice;
    }
    
    public int getTenantUserId() {
        return tenantUserId;
    }

    public void setTenantUserId(int tenantUserId) {
        this.tenantUserId = tenantUserId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public int getContractID() {
        return contractID;
    }

    public void setContractID(int contractID) {
        this.contractID = contractID;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isExpired() {
        if (this.endDate == null) return false;
        java.util.Date today = new java.util.Date();
        return this.endDate.before(today); 
    }
}