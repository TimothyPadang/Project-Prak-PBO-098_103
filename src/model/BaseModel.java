package model;

/**
 * BaseModel - Kelas Abstrak sebagai dasar semua model
 * Implementasi: ABSTRAKSI (abstract class dengan abstract methods)
 * Basis untuk INHERITANCE oleh User, Task, Category, dll.
 */
public abstract class BaseModel {
    // Enkapsulasi: protected agar subclass bisa akses
    protected int id;
    protected String createdAt;

    public BaseModel() {}

    public BaseModel(int id) {
        this.id = id;
    }

    // Abstract methods - wajib diimplementasikan subclass (ABSTRAKSI)
    public abstract String getDisplayName();
    public abstract boolean isValid();

    // Getter & Setter (ENKAPSULASI)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    // POLYMORPHISM: override toString di setiap subclass
    @Override
    public String toString() {
        return getDisplayName();
    }
}
