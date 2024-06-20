package com.files.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.files.model.Products;
import com.files.model.Sales;

@Repository
public interface ProductRepository extends JpaRepository<Products, Long> {
    List<Products> findByCategory_Id(Long categoryId);
    
  Optional<Products> findByProductNameAndProductBrand(String name,String brand);
   
   Page<Products> findAll(Specification<Products> spec, Pageable pageable);
   
   Optional<Products> findByProductName(String name);
   
   
   @Query("SELECT p FROM Products p ORDER BY p.id DESC")
   List<Products> findRecentProducts();

   @Query("SELECT p FROM Products p JOIN p.sales s GROUP BY p ORDER BY SUM(s.quantity) DESC")
   List<Products> findMostSoldProducts();

   @Query("SELECT SUM(s.totalPrice) FROM Sales s WHERE s.saleDate BETWEEN :startDate AND :endDate")
   Double findTotalSalesBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

   @Query("SELECT s FROM Sales s WHERE s.saleDate BETWEEN :startDate AND :endDate")
   List<Sales> findSalesBetweenDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}