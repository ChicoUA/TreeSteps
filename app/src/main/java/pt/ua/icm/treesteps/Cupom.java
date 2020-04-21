package pt.ua.icm.treesteps;

public class Cupom {
    private String name;
    private String description;
    private int price;

    public Cupom(String name, String description, int price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }


    public Cupom() {
        this.name = "";
        this.description = "";
        this.price = 0;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Cupom{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}
