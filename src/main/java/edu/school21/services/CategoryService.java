package edu.school21.services;

import edu.school21.models.Category;
import edu.school21.repositories.CategoryRepository;
import org.springframework.stereotype.Component;

@Component
public class CategoryService {
    CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category saveIfNotExist(final String name) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> categoryRepository.save(new Category(name)));
    }
}
