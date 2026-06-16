package Model;

public class InvoiceItem {
    private int itemId;
    private int invoiceId;
    private String description;
    private int quantity;
    private double unitPrice;
    private double amount;

    public InvoiceItem() {
    }

    public InvoiceItem(int itemId, int invoiceId, String description, int quantity, double unitPrice, double amount) {
        this.itemId = itemId;
        this.invoiceId = invoiceId;
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = amount;
    }
    

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public int getInvoiceId() { return invoiceId; }
    public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}