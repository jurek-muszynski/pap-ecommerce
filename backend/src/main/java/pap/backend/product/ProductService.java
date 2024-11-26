package pap.backend.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException(
                        "product with id " + productId + " does not exist"
                ));
    }

    public void addNewProduct(Product product) {
        Optional<Product> productOptional = productRepository.findProductByName(product.getName());
        if (productOptional.isPresent()) {
            throw new IllegalStateException("product name taken");
        }
        productRepository.save(product);
    }

    public void deleteProduct(Long productId) {
        boolean exists = productRepository.existsById(productId);
        if (!exists) {
            throw new IllegalStateException("product with id " + productId + " does not exist");
        }
        productRepository.deleteById(productId);
    }

    @Transactional
    public void updateProduct(Long productId, String name, String description, double price) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException(
                        "product with id " + productId + " does not exist"
                ));

        if (name != null && !name.isEmpty() && !product.getName().equals(name)) {
            product.setName(name);
        }

        if (description != null && !description.isEmpty() && !product.getDescription().equals(description)) {
            product.setDescription(description);
        }

        if (product.getPrice() != price) {
            product.setPrice(price);
        }
    }
}
