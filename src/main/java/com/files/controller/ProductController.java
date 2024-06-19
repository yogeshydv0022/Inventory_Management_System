package com.files.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.files.model.Products;
import com.files.services.ProductService;

@RestController
@RequestMapping("/ims/products")
public class ProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private PagedResourcesAssembler<Products> pagedResourcesAssembler;

	// create product By Category Id
	@PostMapping("/{categoryId}")
	public ResponseEntity<Products> addProduct(@PathVariable Long categoryId, @RequestPart Products product,
			@RequestPart MultipartFile image) throws IOException {
		Products createdProduct = productService.addProduct(categoryId, product, image);
		return ResponseEntity.ok(createdProduct);
	}

	// get product By id
	@GetMapping("/{id}")
	public ResponseEntity<?> getProductById(@PathVariable Long id) {
		try {
			Products product = productService.getProductById(id);
			return ResponseEntity.ok(product);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	// update product by id
	@PutMapping("/{id}/{categoryId}")
	public ResponseEntity<?> updateProduct(@PathVariable Long id, @PathVariable Long categoryId,
			@RequestPart Products product, @RequestPart(required = false) MultipartFile image) throws IOException {
		try {
			Products updatedProduct = productService.updateProduct(id, categoryId, product, image);
			return ResponseEntity.ok(updatedProduct);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
	}

	// delete product by id
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
		try {
			String message = productService.deleteProduct(id);
			return ResponseEntity.ok(message);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	// get all Products with filtering version 1
	@GetMapping("/v1/")
	public ResponseEntity<Page<Products>> findByBookCriteria(@RequestParam(required = false) List<String> categoryNames,
			@RequestParam(required = false) String searchQuery, @RequestParam(required = false) String productName,
			@RequestParam(required = false) List<String> productBrand, @RequestParam(required = false) Double minPrice,
			@RequestParam(required = false) Double maxPrice, @RequestParam(defaultValue = "0") int pageNumber,
			@RequestParam(defaultValue = "10") int pageSize, @RequestParam(defaultValue = "id") String sortBy,
			@RequestParam(defaultValue = "ASC") String sortOrder) {
		Page<Products> products = productService.findByBookCriteria(categoryNames, searchQuery, productName,
				productBrand, minPrice, maxPrice, pageNumber, pageSize, sortBy, sortOrder);
		return ResponseEntity.ok(products);
	}

	// get all Products with filtering version 2
	@GetMapping("/v2/")
	public PagedModel<EntityModel<Products>> getProducts(@PageableDefault(page = 0, size = 10) Pageable pageable) {
		try {
			Page<Products> productPage = productService.findAll(pageable);
			return pagedResourcesAssembler.toModel(productPage);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT, e.getMessage());
		}
	}

	// get all Products with filtering version 3
	@GetMapping("/v3/")
	public PagedModel<EntityModel<Products>> findProductsByCriteria(
			@RequestParam(required = false) List<String> categoryNames,
			@RequestParam(required = false) String searchQuery, @RequestParam(required = false) String productName,
			@RequestParam(required = false) List<String> productBrand, @RequestParam(required = false) Double minPrice,
			@RequestParam(required = false) Double maxPrice,
			@RequestParam(required = false, defaultValue = "asc") String sortDirection,
			@RequestParam(required = false, defaultValue = "id") String sortBy,
			@PageableDefault(page = 0, size = 10) Pageable pageable) {
		Sort sort = Sort.by(sortDirection.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
		pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

		Page<Products> productPage = productService.findAllByCriteria(categoryNames, searchQuery, productName,
				productBrand, minPrice, maxPrice, pageable);
		return pagedResourcesAssembler.toModel(productPage);
	}

}
