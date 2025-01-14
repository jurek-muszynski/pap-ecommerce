package pap.backend.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pap.backend.config.JwtService;
import pap.backend.order.Order;
import pap.backend.order.OrderController;
import pap.backend.order.OrderService;
import pap.backend.order.PlaceOrderRequest;
import pap.backend.user.User;
import pap.backend.user.UserRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private OrderService orderService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Order testOrder;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setEmail("testuser@example.com");

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUser(testUser);
        testOrder.setDate(LocalDate.now());
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    void getOrders_ShouldReturnAllOrders() throws Exception {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.getOrders()).thenReturn(orders);

        mockMvc.perform(get("/api/v1/order/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    void getOrder_ShouldReturnOrderById() throws Exception {
        when(orderService.getOrder(1L)).thenReturn(testOrder);

        mockMvc.perform(get("/api/v1/order/get/{orderId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    void getOrdersByUserId_ShouldReturnOrdersForUser() throws Exception {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.getOrdersByUserId(1L)).thenReturn(orders);

        mockMvc.perform(get("/api/v1/order/allWithUserId/{userId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    void placeOrder_ShouldAddNewOrder() throws Exception {
        PlaceOrderRequest placeOrderRequest = new PlaceOrderRequest();
        placeOrderRequest.setUserId(1L);
        placeOrderRequest.setEmail("testuser@example.com");
        placeOrderRequest.setDeliveryAddress("123 Test St");

        // Mock the behavior of the service
        doNothing().when(orderService).placeOrder(Mockito.any(PlaceOrderRequest.class));

        mockMvc.perform(post("/api/v1/order/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(placeOrderRequest))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().string("Order added"));
    }


    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    void deleteOrder_ShouldDeleteOrder() throws Exception {
        doNothing().when(orderService).deleteOrder(1L);

        mockMvc.perform(delete("/api/v1/order/delete/{orderId}", 1L).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Order deleted"));
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    void updateOrder_ShouldUpdateOrder() throws Exception {
        doNothing().when(orderService).updateOrder(1L, 1L, LocalDate.now());

        mockMvc.perform(put("/api/v1/order/update/{orderId}", 1L)
                        .param("userId", "1")
                        .param("date", LocalDate.now().toString())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Order updated"));
    }
}
