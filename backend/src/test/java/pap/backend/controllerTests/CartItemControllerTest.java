package pap.backend.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pap.backend.cart.Cart;
import pap.backend.cart.CartService;
import pap.backend.cartItem.CartItem;
import pap.backend.cartItem.CartItemController;
import pap.backend.cartItem.CartItemDTO;
import pap.backend.cartItem.CartItemService;
import pap.backend.config.JwtService;
import pap.backend.product.Product;
import pap.backend.product.ProductService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartItemController.class)
class CartItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartItemService cartItemService;

    @MockBean
    private ProductService productService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private CartService cartService;

    @Autowired
    private ObjectMapper objectMapper;

    private Cart testCart;
    private Product testProduct;
    private CartItem testCartItem;

    @BeforeEach
    void setUp() {
        testCart = new Cart();
        testCart.setId(1L);
        testCart.setLastUpdate(LocalDate.now());

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");

        testCartItem = new CartItem();
        testCartItem.setId(1L);
        testCartItem.setCart(testCart);
        testCartItem.setProduct(testProduct);
        testCartItem.setQuantity(2);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void getCartItems_ShouldReturnAllCartItems() throws Exception {
        List<CartItem> cartItems = Arrays.asList(testCartItem);
        when(cartItemService.getCartItems()).thenReturn(cartItems);

        mockMvc.perform(get("/api/v1/cartItem/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].quantity").value(2));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void getCartItem_ShouldReturnCartItemById() throws Exception {
        when(cartItemService.getCartItem(1L)).thenReturn(testCartItem);

        mockMvc.perform(get("/api/v1/cartItem/get/{cartItemId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void addNewCartItem_ShouldAddCartItem() throws Exception {
        CartItemDTO cartItemDTO = new CartItemDTO(1L, 1L, 1L, 1);

        when(productService.getProduct(1L)).thenReturn(testProduct);
        when(cartService.getCart(1L)).thenReturn(testCart);
        doNothing().when(cartItemService).addNewCartItem(any(CartItem.class));

        mockMvc.perform(post("/api/v1/cartItem/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartItemDTO))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().string("Product added to cart successfully"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void deleteCartItem_ShouldDeleteCartItem() throws Exception {
        doNothing().when(cartItemService).deleteCartItem(1L);

        mockMvc.perform(delete("/api/v1/cartItem/delete/{cartItemId}", 1L).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("CartItem deleted successfully"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    void updateCartItem_ShouldUpdateCartItem() throws Exception {
        Map<String, Object> updates = Map.of("quantity", 3);

        doNothing().when(cartItemService).updateCartItem(1L, null, 3, null);

        mockMvc.perform(put("/api/v1/cartItem/update/{cartItemId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("CartItem updated successfully"));
    }
}
