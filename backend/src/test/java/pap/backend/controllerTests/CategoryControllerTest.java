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
import pap.backend.category.CategoryController;
import pap.backend.category.CategoryService;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<Category> categoryList;

    @BeforeEach
    void setUp() {
        categoryList = Arrays.asList(
                new Category("Electronics"),
                new Category("Books")
        );
    }

    @Test
    void getCategories_ShouldReturnAllCategories() throws Exception {
        Mockito.when(categoryService.getCategories()).thenReturn(categoryList);

        mockMvc.perform(get("/api/v1/category/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Electronics")))
                .andExpect(jsonPath("$[1].name", is("Books")));
    }

    @Test
    void getCategory_ShouldReturnCategoryById() throws Exception {
        Mockito.when(categoryService.getCategory(1L)).thenReturn(categoryList.get(0));

        mockMvc.perform(get("/api/v1/category/get/{categoryId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Electronics")));
    }

    @Test
    void getCategory_ShouldReturnNotFoundForInvalidId() throws Exception {
        Mockito.when(categoryService.getCategory(99L)).thenThrow(new NoSuchElementException("Category not found"));
    }

    @Test
    void addNewCategory_ShouldCreateCategory() throws Exception {
        Category newCategory = new Category("Clothing");

        mockMvc.perform(post("/api/v1/category/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Category added successfully"));

        Mockito.verify(categoryService).addNewCategory(Mockito.any(Category.class));
    }

    @Test
    void addNewCategory_ShouldReturnBadRequestWhenNameIsTaken() throws Exception {
        Category newCategory = new Category("Electronics");

        Mockito.doThrow(new IllegalStateException("Category name taken"))
                .when(categoryService).addNewCategory(Mockito.any(Category.class));

        mockMvc.perform(post("/api/v1/category/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Category name taken"));
    }

    @Test
    void deleteCategory_ShouldDeleteCategory() throws Exception {
        mockMvc.perform(delete("/api/v1/category/delete/{categoryId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Category deleted successfully"));

        Mockito.verify(categoryService).deleteCategory(1L);
    }

    @Test
    void deleteCategory_ShouldReturnNotFoundForInvalidId() throws Exception {
        Mockito.doThrow(new NoSuchElementException("Category not found"))
                .when(categoryService).deleteCategory(99L);

        mockMvc.perform(delete("/api/v1/category/delete/{categoryId}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Category not found"));
    }

    @Test
    void updateCategory_ShouldUpdateCategoryName() throws Exception {
        mockMvc.perform(put("/api/v1/category/update/{categoryId}", 1L)
                        .param("name", "Updated Name"))
                .andExpect(status().isOk())
                .andExpect(content().string("Category updated successfully"));

        Mockito.verify(categoryService).updateCategory(1L, "Updated Name");
    }

    @Test
    void updateCategory_ShouldReturnNotFoundForInvalidId() throws Exception {
        Mockito.doThrow(new NoSuchElementException("Category not found"))
                .when(categoryService).updateCategory(99L, "New Name");

        mockMvc.perform(put("/api/v1/category/update/{categoryId}", 99L)
                        .param("name", "New Name"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Category not found"));
    }
}
