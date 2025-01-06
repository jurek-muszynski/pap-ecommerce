package pap.frontend.models;

import java.time.LocalDate;

public class Cart {
    private Long id; // Unique identifier for the cart
    private User user; // Associated user
    private LocalDate lastUpdate; // Last update date for the cart

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDate lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", user=" + user +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
}
