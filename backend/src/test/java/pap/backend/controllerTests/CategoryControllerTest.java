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
import pap.backend.category.CategoryController;
import pap.backend.category.CategoryService;
import pap.backend.config.JwtService;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private Category electronicsCategory;
    private Category booksCategory;

    @BeforeEach
    void setUp() {
        electronicsCategory = new Category("Electronics");
        booksCategory = new Category("Books");
    }

    @BeforeEach
    void setUpAuthentication() {
        // Mock an authenticated admin user
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("admin@example.com", null, List.of(() -> "ROLE_ADMIN"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void getCategories_ShouldReturnAllCategories() throws Exception {
        List<Category> categories = Arrays.asList(electronicsCategory, booksCategory);
        Mockito.when(categoryService.getCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/v1/category/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Electronics"))
                .andExpect(jsonPath("$[1].name").value("Books"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void getCategory_ShouldReturnCategoryById() throws Exception {
        Mockito.when(categoryService.getCategory(1L)).thenReturn(electronicsCategory);

        mockMvc.perform(get("/api/v1/category/get/{categoryId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Electronics"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void addNewCategory_ShouldAddCategory() throws Exception {
        Mockito.doNothing().when(categoryService).addNewCategory(Mockito.any(Category.class));

        mockMvc.perform(post("/api/v1/category/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(electronicsCategory))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().string("Category added successfully"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void deleteCategory_ShouldDeleteCategory() throws Exception {
        Mockito.doNothing().when(categoryService).deleteCategory(1L);

        mockMvc.perform(delete("/api/v1/category/delete/{categoryId}", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Category deleted successfully"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void updateCategory_ShouldUpdateCategoryName() throws Exception {
        Mockito.doNothing().when(categoryService).updateCategory(1L, "Updated Category");

        mockMvc.perform(put("/api/v1/category/update/{categoryId}", 1L)
                        .param("name", "Updated Category")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Category updated successfully"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void deleteCategory_ShouldReturnNotFoundForInvalidId() throws Exception {
        Mockito.doThrow(new NoSuchElementException("Category not found")).when(categoryService).deleteCategory(99L);

        mockMvc.perform(delete("/api/v1/category/delete/{categoryId}", 99L)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Category not found"));
    }
}
