package pap.backend.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pap.backend.product.Product;
import pap.backend.product.ProductService;

import java.util.List;

@RestController
@RequestMapping("api/v1/category")
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;

    @Autowired
    public CategoryController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping("/all")
    public List<Category> getCategories() {
        return categoryService.getCategories();
    }

    @GetMapping("/get/{categoryId}")
    public Category getCategory(@PathVariable("categoryId") Long categoryId) {
        return categoryService.getCategory(categoryId);
    }

    @PostMapping("/add")
    public void addNewCategory(@RequestBody Category category) {
        categoryService.addNewCategory(category);
    }

    @DeleteMapping("/delete/{categoryId}")
    public void deleteCategory(@PathVariable("categoryId") Long categoryId) {
        categoryService.deleteCategory(categoryId);
    }

    @PutMapping("/update/{categoryId}")
    public void updateCategory(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam(required = false) String name) {
        categoryService.updateCategory(categoryId, name);
    }

    @GetMapping("/{categoryId}/products")
    public List<Product> getProductsByCategoryId(@PathVariable Long categoryId) {
        return productService.getProductsByCategoryId(categoryId);
    }
}
