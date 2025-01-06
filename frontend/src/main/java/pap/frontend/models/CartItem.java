package pap.frontend.models;

public class CartItem {
    private Long id;
    private Long productId;
    private Long cartId;

    public Long getId() {
        return id;
    }

    // Getters and setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    // toString
    @Override
    public String toString() {
        return "CartItem{" +
                "productId=" + productId +
                ", cartId=" + cartId +
                '}';
    }
}
