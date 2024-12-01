package pap.backend.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pap.backend.category.Category;
import pap.backend.category.CategoryRepository;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/v1/product")
public class ProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductController(ProductService productService, CategoryRepository categoryRepository) {

        this.productService = productService;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Product>> getProducts() {
        return new ResponseEntity<List<Product>>(productService.getProducts(), HttpStatus.OK);
    }

    @GetMapping("/get/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable("productId") Long productId) {
        return new ResponseEntity<Product>(productService.getProduct(productId), HttpStatus.OK);
    }

    @GetMapping("/allWithCategoryId/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategoryId(@PathVariable Long categoryId) {
        return new ResponseEntity<List<Product>>(productService.getProductsByCategoryId(categoryId), HttpStatus.OK);
    }

    @GetMapping("/withName/{productName}")
    public ResponseEntity<Product> getProductByName(@PathVariable("productName") String productName){
        return new ResponseEntity<Product>(productService.getProductByName(productName), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addNewProduct(@RequestBody Product product) { // w ciele kategorii podajemy tylko id, reszta pobierana z encji Category

        Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new IllegalStateException("Category not found"));

        product.setCategory(category);

        try {
            productService.addNewProduct(product);
            return new ResponseEntity<String>("Product added successfully", HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<String>("Error adding product", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable("productId") Long productId) {
        try {
            productService.deleteProduct(productId);
            return new ResponseEntity<String>("Product deleted successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<String>("Error deleting product", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<String> updateProduct(
            @PathVariable("productId") Long productId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) Long categoryId) {

        try {
            productService.updateProduct(productId, name, description, imageUrl, price, quantity, categoryId);
            return new ResponseEntity<>("Product updated successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating product", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
