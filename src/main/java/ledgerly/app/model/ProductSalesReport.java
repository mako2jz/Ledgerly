package ledgerly.app.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ProductSalesReport {
    private final StringProperty productName;
    private final IntegerProperty salesCount;
    private final DoubleProperty totalValue;

    public ProductSalesReport(String productName, int salesCount, double totalValue) {
        this.productName = new SimpleStringProperty(productName);
        this.salesCount = new SimpleIntegerProperty(salesCount);
        this.totalValue = new SimpleDoubleProperty(totalValue);
    }

    public String getProductName() {
        return productName.get();
    }

    public StringProperty productNameProperty() {
        return productName;
    }

    public int getSalesCount() {
        return salesCount.get();
    }

    public IntegerProperty salesCountProperty() {
        return salesCount;
    }

    public double getTotalValue() {
        return totalValue.get();
    }

    public DoubleProperty totalValueProperty() {
        return totalValue;
    }
}
