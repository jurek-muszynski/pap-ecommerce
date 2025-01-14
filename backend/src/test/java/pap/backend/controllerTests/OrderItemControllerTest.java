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
import org.springframework.test.web.servlet.MockMvc;
import pap.backend.config.JwtService;
import pap.backend.orderItem.OrderItem;
import pap.backend.orderItem.OrderItemController;
import pap.backend.orderItem.OrderItemService;
import pap.backend.product.Product;
import pap.backend.order.Order;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderItemController.class)
class OrderItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private OrderItemService orderItemService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        Product testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");

        Order testOrder = new Order();
        testOrder.setId(1L);

        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setProduct(testProduct);
        testOrderItem.setOrder(testOrder);
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    void getOrderItems_ShouldReturnAllOrderItems() throws Exception {
        List<OrderItem> orderItems = Arrays.asList(testOrderItem);
        when(orderItemService.getOrderItems()).thenReturn(orderItems);

        mockMvc.perform(get("/api/v1/orderItem/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    void getOrderItem_ShouldReturnOrderItemById() throws Exception {
        when(orderItemService.getOrderItem(1L)).thenReturn(testOrderItem);

        mockMvc.perform(get("/api/v1/orderItem/get/{orderItemId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    void addNewOrderItem_ShouldAddOrderItem() throws Exception {
        doNothing().when(orderItemService).addNewOrderItem(Mockito.any(OrderItem.class));

        mockMvc.perform(post("/api/v1/orderItem/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testOrderItem))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().string("OrderItem added successfully"));
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    void deleteOrderItem_ShouldDeleteOrderItemById() throws Exception {
        doNothing().when(orderItemService).deleteOrderItem(1L);

        mockMvc.perform(delete("/api/v1/orderItem/delete/{orderItemId}", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("OrderItem deleted successfully"));
    }

    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    void updateOrderItem_ShouldUpdateOrderItem() throws Exception {
        doNothing().when(orderItemService).updateOrderItem(1L, 1L, 1L);

        mockMvc.perform(put("/api/v1/orderItem/update/{orderItemId}", 1L)
                        .param("productId", "1")
                        .param("orderId", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("OrderItem updated successfully"));
    }
}
