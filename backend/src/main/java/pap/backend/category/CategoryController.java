package pap.backend.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pap.backend.product.Product;
import pap.backend.product.ProductService;

import java.util.List;
import java.util.NoSuchElementException;

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
    public ResponseEntity<List<Category>> getCategories() {
        return new ResponseEntity<List<Category>>(categoryService.getCategories(), HttpStatus.OK);
    }

    @GetMapping("/get/{categoryId}")
    public ResponseEntity<Category> getCategory(@PathVariable("categoryId") Long categoryId) {
        return new ResponseEntity<Category>(categoryService.getCategory(categoryId), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addNewCategory(@RequestBody Category category) {
        try {
            categoryService.addNewCategory(category);
            return new ResponseEntity<String>("Category added successfully", HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<String>("Error adding category", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable("categoryId") Long categoryId) {
        try {
            categoryService.deleteCategory(categoryId);
            return new ResponseEntity<String>("Category deleted successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<String>("Error deleting category", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{categoryId}")
    public ResponseEntity<String> updateCategory(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam(required = false) String name) {
        try {
            categoryService.updateCategory(categoryId, name);
            return new ResponseEntity<String>("Category updated successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<String>("Error updating category", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
