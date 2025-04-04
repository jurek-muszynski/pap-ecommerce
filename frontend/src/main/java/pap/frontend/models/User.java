package pap.frontend.models;

public class User {
    private Long id;
    private String username;
    private String email;
    private String name;
    private String password;
    private UserRole role; // Role can be "CUSTOMER" or "ADMIN"

    public User() {
    }

    public User(String username, String email, String password, UserRole role, String name) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.name = name;
    }

    public Long getId() { return id; }

    public String getUsername() { return email; }

    public String getName() { return name; }

    public void setUsername(String username) { this.username = username; }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role + '\'' +
                ", name=" + name + '\'' +
                '}';
    }
}
