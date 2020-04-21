package pt.ua.icm.treesteps;

public class BoughtCupom {
    private String name;
    private String description;
    private int price;
    private boolean used;

    public BoughtCupom() {
        this.name = "";
        this.description = "";
        this.price = 0;
        this.used = false;
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

    public boolean getUsed(){
        return this.used;
    }

    public void setUsed(boolean used){
        this.used = used;
    }

    @Override
    public String toString() {
        return "BoughtCupom{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", used=" + used +
                '}';
    }
}
