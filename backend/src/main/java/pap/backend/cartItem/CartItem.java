package pap.backend.cartItem;

import jakarta.persistence.*;
import pap.backend.cart.Cart;
import pap.backend.product.Product;

@Entity
@Table(name = "cartItems")
public class CartItem {

    @Id
    @SequenceGenerator(
            name = "cartItem_sequence",
            sequenceName = "cartItem_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "cartItem_sequence"
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    public CartItem() {}

    public CartItem(Product product, Cart cart) {
        this.product = product;
        this.cart = cart;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", product=" + product +
                ", cart=" + cart +
                '}';
    }
}
