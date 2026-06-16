package Model;

import java.sql.Date;

public class Invoice {

    private int invoiceId;
    private int contractId;
    private String roomNumber;
    private int invoiceMonth;
    private int invoiceYear;
    private Date issueDate;
    private Date dueDate;
    private double totalAmount;
    private String status;

    public Invoice() {
    }

    public Invoice(int invoiceId, int contractId, String roomNumber, int invoiceMonth, int invoiceYear, Date issueDate, Date dueDate, double totalAmount, String status) {
        this.invoiceId = invoiceId;
        this.contractId = contractId;
        this.roomNumber = roomNumber;
        this.invoiceMonth = invoiceMonth;
        this.invoiceYear = invoiceYear;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getInvoiceMonth() {
        return invoiceMonth;
    }

    public void setInvoiceMonth(int invoiceMonth) {
        this.invoiceMonth = invoiceMonth;
    }

    public int getInvoiceYear() {
        return invoiceYear;
    }

    public void setInvoiceYear(int invoiceYear) {
        this.invoiceYear = invoiceYear;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
