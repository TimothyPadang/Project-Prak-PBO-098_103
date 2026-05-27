package model;

/**
 * Category Model
 * INHERITANCE: extends BaseModel
 */
public class Category extends BaseModel {
    private String name;
    private String description;

    public Category() { super(); }

    public Category(int id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }

    @Override
    public String getDisplayName() { return name; }

    @Override
    public boolean isValid() { return name != null && !name.trim().isEmpty(); }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Kompatibilitas lama: fitur warna kategori sudah dihapus.
    public String getColor() { return null; }
    public void setColor(String color) { /* diabaikan */ }

    @Override
    public String toString() { return name; }
}
