package pap.backend.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import pap.backend.category.Category;
import pap.backend.category.CategoryRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException(
                        "product with id " + productId + " does not exist"
                ));
    }

    public Product addNewProduct(Product product) {
        Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new IllegalStateException("Category not found"));

        product.setCategory(category);

        Optional<Product> productOptional = productRepository.findProductByName(product.getName());
        if (productOptional.isPresent()) {
            throw new IllegalStateException("product name taken");
        }
        productRepository.save(product);

        return product;
    }

    public void deleteProduct(Long productId) {
        boolean exists = productRepository.existsById(productId);
        if (!exists) {
            throw new NoSuchElementException("product with id " + productId + " does not exist");
        }
        productRepository.deleteById(productId);
    }

    @Transactional
    public Product updateProduct(Long productId, String name, String description,
                              String imageUrl, Double price, Integer quantity, Long categoryId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException(
                        "product with id " + productId + " does not exist"
                ));

        if (name != null && !name.isEmpty() && !product.getName().equals(name)) {
            product.setName(name);
        }

        if (description != null && !description.isEmpty() && !product.getDescription().equals(description)) {
            product.setDescription(description);
        }

        if (imageUrl != null && !imageUrl.isEmpty() && !product.getImageUrl().equals(imageUrl)) {
            product.setImageUrl(imageUrl);
        }

        if (price != null && price > 0 && !product.getPrice().equals(price)) {
            product.setPrice(price);
        }

        if (quantity != null && quantity > 0 && !product.getQuantity().equals(quantity)) {
            product.setQuantity(quantity);
        }

        if (categoryId != null) {
            Category existingCategory = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalStateException(
                            "category with id " + categoryId + " does not exist"
                    ));
            if (!product.getCategory().equals(existingCategory)) {
                product.setCategory(existingCategory);
            }
        }

        return product;
    }

    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findProductsByCategoryId(categoryId);
    }

    public Product getProductByName(String productName) {
        return productRepository.findProductByName(productName)
                .orElseThrow(() -> new NoSuchElementException(
                        "product with name " + productName + " does not exist"
                ));
    }
}
