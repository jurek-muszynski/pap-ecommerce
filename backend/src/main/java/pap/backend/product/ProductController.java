package pap.backend.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pap.backend.category.Category;
import pap.backend.category.CategoryRepository;


import java.util.List;

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
    public List<Product> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/get/{productId}")
    public Product getProduct(@PathVariable("productId") Long productId) {
        return productService.getProduct(productId);
    }

    @PostMapping("/add")
    public void addNewProduct(@RequestBody Product product) { // w ciele kategorii podajemy tylko id, reszta pobierana z encji Category

        Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new IllegalStateException("Category not found"));

        product.setCategory(category);

        productService.addNewProduct(product);
    }

    @DeleteMapping("/delete/{productId}")
    public void deleteProduct(@PathVariable("productId") Long productId) {
        productService.deleteProduct(productId);
    }

    @PutMapping("/update/{productId}")
    public void updateProduct(
            @PathVariable("productId") Long productId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) Integer quantity) {
        productService.updateProduct(productId, name, description, imageUrl, price, quantity);
    }


}
