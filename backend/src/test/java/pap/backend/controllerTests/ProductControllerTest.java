package pap.backend.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pap.backend.category.Category;
import pap.backend.category.CategoryRepository;
import pap.backend.category.CategoryService;
import pap.backend.product.Product;
import pap.backend.product.ProductController;
import pap.backend.product.ProductService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Product> productList;
    private Category electronics;

    @BeforeEach
    void setUp() {
        electronics = new Category("Electronics");
        electronics.setId(1L);

        productList = Arrays.asList(
                new Product("iPhone", "Apple smartphone", "http://example.com/iphone.jpg", 999.99, 10, electronics),
                new Product("Laptop", "High-end laptop", "http://example.com/laptop.jpg", 1299.99, 5, electronics)
        );
    }

    @Test
    void getProducts_ShouldReturnAllProducts() throws Exception {
        Mockito.when(productService.getProducts()).thenReturn(productList);

        mockMvc.perform(get("/api/v1/product/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("iPhone")))
                .andExpect(jsonPath("$[1].name", is("Laptop")));

    }

    @Test
    void getProduct_ShouldReturnProductById() throws Exception {
        Mockito.when(productService.getProduct(1L)).thenReturn(productList.get(0));

        mockMvc.perform(get("/api/v1/product/get/{productId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("iPhone")));
    }

    @Test
    void getProduct_ShouldReturnNotFoundForInvalidId() throws Exception {
        Mockito.when(productService.getProduct(99L)).thenThrow(new NoSuchElementException("Product not found"));
    }

    @Test
    void getProductsByCategoryId_ShouldReturnProductsInCategory() throws Exception {
        Mockito.when(productService.getProductsByCategoryId(1L)).thenReturn(productList);

        mockMvc.perform(get("/api/v1/product/allWithCategoryId/{categoryId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].category.name", is("Electronics")));
    }

    @Test
    void getProductByName_ShouldReturnProduct() throws Exception {
        Mockito.when(productService.getProductByName("iPhone")).thenReturn(productList.get(0));

        mockMvc.perform(get("/api/v1/product/withName/{productName}", "iPhone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("iPhone")));
    }

    @Test
    void addNewProduct_ShouldCreateProduct() throws Exception {
        Product newProduct = new Product("Tablet", "Android tablet", "http://example.com/tablet.jpg", 499.99, 20, electronics);

        Mockito.when(categoryRepository.findById(1L)).thenReturn(java.util.Optional.of(electronics));
        Mockito.when(productService.addNewProduct(Mockito.any(Product.class))).thenReturn(newProduct);

        mockMvc.perform(post("/api/v1/product/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Product added successfully"));
    }

    @Test
    void deleteProduct_ShouldDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/v1/product/delete/{productId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Product deleted successfully"));

        Mockito.verify(productService).deleteProduct(1L);
    }

    @Test
    void deleteProduct_ShouldReturnNotFoundForInvalidId() throws Exception {
        Mockito.doThrow(new NoSuchElementException("Product not found"))
                .when(productService).deleteProduct(99L);

        mockMvc.perform(delete("/api/v1/product/delete/{productId}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Product not found"));
    }

    @Test
    void updateProduct_ShouldUpdateProduct() throws Exception {
        mockMvc.perform(put("/api/v1/product/update/{productId}", 1L)
                        .param("name", "Updated Product"))
                .andExpect(status().isOk())
                .andExpect(content().string("Product updated successfully"));

        Mockito.verify(productService).updateProduct(1L, "Updated Product", null, null, null, null, null);
    }

    @Test
    void updateProduct_ShouldReturnNotFoundForInvalidId() throws Exception {
        Mockito.doThrow(new NoSuchElementException("Product not found"))
                .when(productService).updateProduct(99L, "Updated Product", null, null, null, null, null);

        mockMvc.perform(put("/api/v1/product/update/{productId}", 99L)
                        .param("name", "Updated Product"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Product not found"));
    }
}
