package com.files.service.Interface;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.files.model.Products;

public interface IProduct {
	
	Products addProduct(Long categoryId, Products product, MultipartFile image) throws IOException;
    Products getProductById(Long id);
    Products updateProduct(Long id, Long categoryId, Products product, MultipartFile image) throws IOException;
    String deleteProduct(Long id);
//    List<Products> getAllProducts();
 //   List<Products> getProductsByCategory(Long categoryId);
    Page<Products> findByBookCriteria(List<String> categoryNames, String searchQuery, String productName,
            List<String> productBrand, Double minPrice, Double maxPrice,
            int pageNumber, int pageSize, String sortBy, String sortOrder);

	
}
