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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pap.backend.category.Category;
import pap.backend.category.CategoryRepository;
import pap.backend.config.JwtService;
import pap.backend.product.Product;
import pap.backend.product.ProductController;
import pap.backend.product.ProductRepository;
import pap.backend.product.ProductService;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private ProductService productService;

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category("Electronics");
        product = new Product("Laptop", "High-end laptop", "image_url", 1200.0, 10, category);
        product.setId(1L);
    }

    @BeforeEach
    void setUpAuthentication() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("admin@example.com", null, List.of(() -> "ROLE_ADMIN"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void getProducts_ShouldReturnAllProducts() throws Exception {
        List<Product> products = Arrays.asList(product, new Product("Smartphone", "High-end phone", "image_url_2", 800.0, 5, category));
        Mockito.when(productService.getProducts()).thenReturn(products);

        mockMvc.perform(get("/api/v1/product/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[1].name").value("Smartphone"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void getProduct_ShouldReturnProductById() throws Exception {
        Mockito.when(productService.getProduct(1L)).thenReturn(product);

        mockMvc.perform(get("/api/v1/product/get/{productId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(1200.0));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void getProductsByCategoryId_ShouldReturnProducts() throws Exception {
        List<Product> products = Arrays.asList(product);
        Mockito.when(productService.getProductsByCategoryId(1L)).thenReturn(products);

        mockMvc.perform(get("/api/v1/product/allWithCategoryId/{categoryId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void addNewProduct_ShouldAddProduct() throws Exception {
        // Create mock product and category
        Category mockCategory = new Category("Electronics");
        mockCategory.setId(1L);
        Product newProduct = new Product("New Laptop", "High-end laptop", "image_url", 1500.0, 5, mockCategory);

        // Mock behavior of category repository and product service
        Mockito.when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
        Mockito.when(productService.addNewProduct(Mockito.any(Product.class))).thenReturn(newProduct);

        // Perform POST request
        mockMvc.perform(post("/api/v1/product/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().string("Product added successfully"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void deleteProduct_ShouldDeleteProduct() throws Exception {
        Mockito.doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/v1/product/delete/{productId}", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Product deleted successfully"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void updateProduct_ShouldUpdateProduct() throws Exception {

        Product updatedProduct = new Product("Updated Laptop", "Updated Description", "updated_image_url", 1000.0, 8, category);

        Mockito.when(productService.updateProduct(1L, "Updated Laptop", "Updated Description", "updated_image_url", 1000.0, 8, 1L)).thenReturn(updatedProduct);

        mockMvc.perform(put("/api/v1/product/update/{productId}", 1L)
                        .param("name", "Updated Laptop")
                        .param("description", "Updated Description")
                        .param("imageUrl", "updated_image_url")
                        .param("price", "1000.0")
                        .param("quantity", "8")
                        .param("categoryId", "1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Product updated successfully"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void deleteProduct_ShouldReturnNotFoundForInvalidId() throws Exception {
        Mockito.doThrow(new NoSuchElementException("Product not found")).when(productService).deleteProduct(99L);

        mockMvc.perform(delete("/api/v1/product/delete/{productId}", 99L)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Product not found"));
    }
}
