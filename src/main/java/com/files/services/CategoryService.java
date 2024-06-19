package com.files.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.files.exception.FileNotSupportedException;
import com.files.model.Category;
import com.files.model.Products;
import com.files.repository.CategoryRepository;
import com.files.service.Interface.ICategory;

@Service
public class CategoryService implements ICategory {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ProductService productService;
	
	private static final Path UPLOAD_PATH;

	static {
		try {
			UPLOAD_PATH = Paths.get(new ClassPathResource("").getFile().getAbsolutePath() + File.separator + "static"
					+ File.separator + "image");
			if (!Files.exists(UPLOAD_PATH)) {
				Files.createDirectories(UPLOAD_PATH);
			}
		} catch (IOException e) {
			throw new RuntimeException("Error initializing UPLOAD_PATH", e);
		}
	}
	
	@Override
	public Category addCategory(Category category, MultipartFile image) throws IOException {
		
		Optional<Category> existingProduct = categoryRepository.findByCategoryName(category.getImage());
		if (existingProduct.isPresent()) {
			throw new RuntimeException("Category with the same name already exists");
		}
		
		if (image != null && !image.isEmpty()) {
		if (!image.getContentType().equals("image/jpeg") && !image.getContentType().equals("image/png")
				&& !image.getContentType().equals("image/gif") && !image.getContentType().equals("image/bmp")) {
			throw new FileNotSupportedException("Only .jpeg, .png, .gif, and .bmp images are supported");
		}
		String timeStampedFileName = new SimpleDateFormat("ssmmHHddMMyyyy").format(new Date()) + "_"
				+ image.getOriginalFilename();
		Path filePath = UPLOAD_PATH.resolve(timeStampedFileName);
		Files.copy(image.getInputStream(), filePath);
		String fileUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/image/").path(timeStampedFileName)
				.toUriString();
		category.setImage(fileUri);
		}
		return categoryRepository.save(category);
	}

	@Override
	public Category getCategoryById(Long id) {
		return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
	}

	
	@Override
	public Category updateCategory(Long id, Category category, MultipartFile image) throws IOException {
	    Category existingCategory = categoryRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Category not found"));
	    
	    existingCategory.setCategoryName(category.getCategoryName());
	   
	    if (image != null && !image.isEmpty()) {
	    	if (!image.getContentType().equals("image/jpeg") && !image.getContentType().equals("image/png")
					&& !image.getContentType().equals("image/gif") && !image.getContentType().equals("image/bmp")) {
				throw new FileNotSupportedException("Only .jpeg, .png, .gif, and .bmp images are supported");
	    	}
	        String timeStampedFileName = new SimpleDateFormat("ssmmHHddMMyyyy").format(new Date()) + "_"
	                + image.getOriginalFilename();
	        Path filePath = UPLOAD_PATH.resolve(timeStampedFileName);
	        Files.copy(image.getInputStream(), filePath);
	        String fileUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/image/")
	                .path(timeStampedFileName).toUriString();
	        existingCategory.setImage(fileUri);
	    }
	    return categoryRepository.save(existingCategory);
	}

	@Override
	public String deleteCategory(Long id) {
	Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
		categoryRepository.deleteById(category.getId());
		return "Category with ID: " + category.getId() + " has been deleted successfully";
	}
	
	@Override
	public String deleteCategoryWithProducts(Long id) {
		Category category = categoryRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Category not found"));
		List<Products> products = category.getProducts();
		for (Products product : products) {
			productService.deleteProduct(product.getId());
		}
		categoryRepository.deleteById(id);
		return "Category with ID: " + id + " and associated products have been deleted successfully";
	}
	
	@Override
	public List<Category> getAllCategories() {
	    List<Category> categoryList = categoryRepository.findAll();
	    if (!categoryList.isEmpty()) {
	        return categoryList;
	    } else {
	        throw new RuntimeException("Category list is empty");
	    }
	}
	
	public Page<Category> findAllCategory(Pageable pageable) {
		Page<Category> categoryList=categoryRepository.findAll(pageable);
		if (!categoryList.isEmpty()) {
	        return categoryList;
	    } else {
	        throw new RuntimeException("Category list is empty");
	    }
	}
	
	
	
}