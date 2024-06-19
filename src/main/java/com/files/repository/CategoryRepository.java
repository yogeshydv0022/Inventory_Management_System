package com.files.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.files.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Add any custom query methods if needed
	
	Optional<Category> findByCategoryName(String name);
}