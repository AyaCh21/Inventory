package be.kuleuven.scanner;

public class Product {
    private String idchart;
    private String name;
    private String description;
    private double price;
    private double quantity;
    private String idImage;

    public Product(String idchart, String name, String description, double price, double quantity, String idImage) {
        this.idchart = idchart;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.idImage = idImage;
    }

    public String getIdchart() {
        return idchart;
    }

    public void setIdchart(String idchart) {
        this.idchart = idchart;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getIdImage() {
        return idImage;
    }

    public void setIdImage(String idImage) {
        this.idImage = idImage;
    }

    @Override
    public String toString() {
        return "Product{" +
                "idchart='" + idchart + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", idImage='" + idImage + '\'' +
                '}';
    }
}
