package pap.backend.category;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException(
                        "category with id " + categoryId + " does not exist"
                ));
    }

    public void addNewCategory(Category category) {
        Optional<Category> categoryOptional = categoryRepository.findCategoryByName(category.getName());
        if (categoryOptional.isPresent()) {
            throw new IllegalStateException("category name taken");
        }
        categoryRepository.save(category);
    }

    public void deleteCategory(Long categoryId) {
        boolean exists = categoryRepository.existsById(categoryId);
        if (!exists) {
            throw new IllegalStateException("category with id " + categoryId + " does not exist");
        }
        categoryRepository.deleteById(categoryId);
    }

    @Transactional
    public void updateCategory(Long categoryId, String name) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalStateException(
                        "category with id " + categoryId + " does not exist"
                ));

        if (name != null && !name.isEmpty() && !category.getName().equals(name)) {
            category.setName(name);
        }
    }
}
