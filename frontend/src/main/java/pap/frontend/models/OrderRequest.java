package pap.frontend.models;

public class OrderRequest {
    private Long userId;
    private String email;
    private String deliveryAddress;

    public OrderRequest(Long userId, String email, String deliveryAddress) {
        this.userId = userId;
        this.email = email;
        this.deliveryAddress = deliveryAddress;
    }

    // Getters and setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "OrderRequest{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", deliveryAddress='" + deliveryAddress + '\'' +
                '}';
    }
}
