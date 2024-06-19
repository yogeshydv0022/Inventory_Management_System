package com.files.service.Interface;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.files.model.Category;

public interface ICategory {

	Category addCategory(Category category, MultipartFile image) throws IOException;

	Category getCategoryById(Long id);

	Category updateCategory(Long id, Category category, MultipartFile image) throws IOException;

	String deleteCategory(Long id);

	String deleteCategoryWithProducts(Long id);

	List<Category> getAllCategories();
}
