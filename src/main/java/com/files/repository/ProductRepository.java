package com.files.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.files.model.Products;

@Repository
public interface ProductRepository extends JpaRepository<Products, Long> {
    List<Products> findByCategory_Id(Long categoryId);
    
  Optional<Products> findByProductNameAndProductBrand(String name,String brand);
   
   Page<Products> findAll(Specification<Products> spec, Pageable pageable);
   
   Optional<Products> findByProductName(String name);
}