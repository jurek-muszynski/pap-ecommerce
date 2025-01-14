package pap.backend.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pap.backend.cart.Cart;
import pap.backend.cart.CartController;
import pap.backend.cart.CartService;
import pap.backend.config.JwtService;
import pap.backend.user.User;
import pap.backend.user.UserRepository;
import pap.backend.user.UserRole;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private Cart testCart;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setEmail("testuser@example.com");
        testUser.setRole(UserRole.USER);

        testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        testCart.setLastUpdate(LocalDate.now());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void getCarts_ShouldReturnAllCarts() throws Exception {
        List<Cart> carts = Arrays.asList(testCart, new Cart());
        when(cartService.getCarts()).thenReturn(carts);

        mockMvc.perform(get("/api/v1/cart/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void getCart_ShouldReturnCartById() throws Exception {
        when(cartService.getCart(1L)).thenReturn(testCart);

        mockMvc.perform(get("/api/v1/cart/get/{cartId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void addNewCart_ShouldAddCart() throws Exception {
        Mockito.doNothing().when(cartService).addNewCart(Mockito.any(Cart.class));

        mockMvc.perform(post("/api/v1/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCart))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().string("Cart added"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void deleteCart_ShouldDeleteCart() throws Exception {
        doNothing().when(cartService).deleteCart(1L);

        mockMvc.perform(delete("/api/v1/cart/delete/{cartId}", 1L).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Cart deleted"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void updateCart_ShouldUpdateCart() throws Exception {
        doNothing().when(cartService).updateCart(1L, 1L, LocalDate.now());

        mockMvc.perform(put("/api/v1/cart/update/{cartId}", 1L)
                        .param("userId", "1")
                        .param("lastUpdate", LocalDate.now().toString())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Cart updated"));
    }
}
