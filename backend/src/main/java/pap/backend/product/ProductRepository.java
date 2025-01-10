package pap.backend.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findProductByName(String name);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    List<Product> findProductsByCategoryId(Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.category.id IN :categoryIds AND p.id NOT IN :productIds")
    List<Product> findOtherProductsByCategoryIdsAndExcludeProducts(List<Long> categoryIds, List<Long> productIds);
}
