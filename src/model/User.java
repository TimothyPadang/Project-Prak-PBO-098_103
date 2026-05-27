package model;

/**
 * User Model
 * INHERITANCE: extends BaseModel
 * ENKAPSULASI: semua field private, akses via getter/setter
 */
public class User extends BaseModel {
    private String username;
    private String password;
    private String fullName;
    private String email;

    public User() { super(); }

    public User(int id, String username, String fullName, String email) {
        super(id);
        this.username = username;
        this.fullName = fullName;
        this.email = email;
    }

    @Override
    public String getDisplayName() {
        return fullName + " (" + username + ")";
    }

    @Override
    public boolean isValid() {
        return username != null && !username.trim().isEmpty()
            && password != null && !password.trim().isEmpty()
            && fullName != null && !fullName.trim().isEmpty();
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() { return fullName; }
}
