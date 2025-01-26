package edu.school21.services;

import edu.school21.models.Category;
import edu.school21.repositories.CategoryRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category saveIfNotExist(final String name) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> categoryRepository.save(new Category(name)));
    }

    @Scheduled(cron = "0 */10 * * * *", zone = "Europe/Moscow")
    public void cleanUnusedCategories() {
        categoryRepository.deleteUnusedCategories();
    }
}
