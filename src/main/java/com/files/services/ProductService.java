package com.files.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.files.exception.FileNotSupportedException;
import com.files.model.Category;
import com.files.model.Products;
import com.files.repository.CategoryRepository;
import com.files.repository.ProductRepository;
import com.files.service.Interface.IProduct;

@Service
public class ProductService implements IProduct {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CategoryRepository categoryRepository;

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
	public Products addProduct(Long categoryId, Products product, MultipartFile image) throws IOException {
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new RuntimeException("Category not found"));
		Optional<Products> existingProduct = productRepository
				.findByProductNameAndProductBrand(product.getProductName(), product.getProductBrand());
		if (existingProduct.isPresent()) {
			throw new RuntimeException("A product with the same name and brand already exists");
		}
		product.setCategory(category);

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
		product.setImages(fileUri);
		return productRepository.save(product);
	}

	@Override
	public Products getProductById(Long id) {
		return productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
	}

	@Override
	public Products updateProduct(Long id, Long categoryId, Products product, MultipartFile image) throws IOException {
		Products existingProduct = getProductById(id);
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new RuntimeException("Category not found"));
		existingProduct.setProductName(product.getProductName());
		existingProduct.setProductBrand(product.getProductBrand());
		existingProduct.setQuantity(product.getQuantity());
		existingProduct.setPrice(product.getPrice());
		existingProduct.setCategory(category);

		if (image != null && !image.isEmpty()) {
			// Save product image
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
			existingProduct.setImages(fileUri);
		}
		return productRepository.save(existingProduct);
	}

	@Override
	public String deleteProduct(Long id) {
		Products product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
		productRepository.deleteById(product.getId());
		return "Product with ID: " + product.getId() + " has been deleted successfully";
	}

	@Override
	public Page<Products> findByBookCriteria(List<String> categoryNames, String searchQuery, String productName,
			List<String> productBrand, Double minPrice, Double maxPrice, int pageNumber, int pageSize, String sortBy,
			String sortOrder) {

		Objects.requireNonNull(pageNumber, "Page number cannot be null");
		Objects.requireNonNull(pageSize, "Page size cannot be null");

		if (pageNumber < 0 || pageSize <= 0) {
			throw new IllegalArgumentException("Invalid page number or size");
		}

		Specification<Products> spec = Specification.where(null);

		if (StringUtils.hasText(searchQuery)) {
			spec = spec.and((root, query, builder) -> builder.or(
					builder.like(root.get("category").get("categoryName"), "%" + searchQuery + "%"),
					builder.like(root.get("productName"), "%" + searchQuery + "%"),
					builder.like(root.get("productBrand"), "%" + searchQuery + "%")));
		}

		if (categoryNames != null && !categoryNames.isEmpty()) {
			spec = spec.and((root, query, builder) -> root.join("category").get("categoryName").in(categoryNames));
		}

		if (StringUtils.hasText(productName)) {
			spec = spec.and((root, query, builder) -> builder.like(root.get("productName"), "%" + productName + "%"));
		}

		if (productBrand != null && !productBrand.isEmpty()) {
			spec = spec.and((root, query, builder) -> root.get("productBrand").in(productBrand));
		}

		if (minPrice != null && minPrice > 0) {
			spec = spec.and((root, query, builder) -> builder.greaterThanOrEqualTo(root.get("price"), minPrice));
		}

		if (maxPrice != null && maxPrice < Double.MAX_VALUE) {
			spec = spec.and((root, query, builder) -> builder.lessThanOrEqualTo(root.get("price"), maxPrice));
		}

		Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
		Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

		return productRepository.findAll(spec, pageable);
	}

	public Page<Products> findAll(Pageable pageable) {
		return productRepository.findAll(pageable);
	}

	public Page<Products> findAllByCriteria(List<String> categoryNames, String searchQuery, String productName,
			List<String> productBrand, Double minPrice, Double maxPrice, Pageable pageable) {

		Specification<Products> spec = Specification.where(null);

		if (StringUtils.hasText(searchQuery)) {
			spec = spec.and((root, query, builder) -> builder.or(
					builder.like(root.get("category").get("categoryName"), "%" + searchQuery + "%"),
					builder.like(root.get("productName"), "%" + searchQuery + "%"),
					builder.like(root.get("productBrand"), "%" + searchQuery + "%")));
		}

		if (categoryNames != null && !categoryNames.isEmpty()) {
			spec = spec.and((root, query, builder) -> root.join("category").get("categoryName").in(categoryNames));
		}

		if (StringUtils.hasText(productName)) {
			spec = spec.and((root, query, builder) -> builder.like(root.get("productName"), "%" + productName + "%"));
		}

		if (productBrand != null && !productBrand.isEmpty()) {
			spec = spec.and((root, query, builder) -> root.get("productBrand").in(productBrand));
		}

		if (minPrice != null && minPrice > 0) {
			spec = spec.and((root, query, builder) -> builder.greaterThanOrEqualTo(root.get("price"), minPrice));
		}

		if (maxPrice != null && maxPrice < Double.MAX_VALUE) {
			spec = spec.and((root, query, builder) -> builder.lessThanOrEqualTo(root.get("price"), maxPrice));
		}
		
		return productRepository.findAll(spec, pageable);
	}

}
