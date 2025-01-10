package pap.backend.cartItem;

public class CartItemDTO {
    private Long id;
    private Long productId;
    private Long cartId;
    private Integer quantity;

    // Constructor
    public CartItemDTO(Long id, Long productId, Long cartId, Integer quantity) {
        this.id = id;
        this.productId = productId;
        this.cartId = cartId;
        this.quantity = quantity;
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

    public Integer getQuantity() { return quantity; }

    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
