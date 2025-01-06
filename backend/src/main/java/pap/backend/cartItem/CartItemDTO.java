package pap.backend.cartItem;

public class CartItemDTO {
    private Long id;
    private Long productId;
    private Long cartId;

    // Constructor
    public CartItemDTO(Long id, Long productId, Long cartId) {
        this.id = id;
        this.productId = productId;
        this.cartId = cartId;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
}
