package com.files.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.files.model.Products;
import com.files.model.Sales;

@Repository
public interface SalesRepository extends JpaRepository<Sales, Long> {
	
	 List<Sales> findByProductAndSaleDateBetween(Products product, LocalDate startDate, LocalDate endDate);
}