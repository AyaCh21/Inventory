package be.kuleuven.scanner;

public class Orders {
    String id;
    String product;
    String quantity;
    String supplier;
    String cost;

    public Orders(String id, String product, String quantity, String supplier, String cost) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.supplier = supplier;
        this.cost = cost;
    }

    public String getId(){return id; }

    public String getProduct() {
        return product;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getSupplier() {
        return supplier;
    }

    public String getCost() { return cost; }
}
