package ledgerly.app.model;

public class Sale {
    private final int saleId;
    private final String createdAt;
    private final double amount;
    private final String description;
    private final String productName;

    public Sale(int saleId, String createdAt, double amount, String description, String productName) {
        this.saleId = saleId;
        this.createdAt = createdAt;
        this.amount = amount;
        this.description = description;
        this.productName = productName;
    }

    public int getSaleId() { return saleId; }
    public String getCreatedAt() { return createdAt; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getProductName() { return productName; }
}
