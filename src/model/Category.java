package model;

/**
 * Category Model
 * INHERITANCE: extends BaseModel
 */
public class Category extends BaseModel {
    private String name;
    private String description;
    private String color;

    public Category() { super(); }

    public Category(int id, String name, String description, String color) {
        super(id);
        this.name = name;
        this.description = description;
        this.color = color;
    }

    @Override
    public String getDisplayName() { return name; }

    @Override
    public boolean isValid() {
        return name != null && !name.isEmpty();
    }

    // Getters & Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    @Override
    public String toString() { return name; }
}
