package model;

public class Brand {
    private int brandId;
    private String name;
    private String description;

    public Brand() {

    }

    public Brand(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Brand = {" +
                "brandId = " + brandId +
                ", name = '" + name + '\'' +
                ", description = '" + description + '\'' +
                '}';
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {
        this.brandId = brandId;
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
}
