package com.files.services;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.files.model.Products;
import com.files.model.Sales;
import com.files.payload.ComparisonResponse;
import com.files.payload.ComparisonResult;
import com.files.payload.SalesDTO;
import com.files.repository.ProductRepository;
import com.files.repository.SalesRepository;

@Service
public class SalesService {
    
	  @Autowired
	    private ProductRepository productsRepository;

	    @Autowired
	    private SalesRepository salesRepository;

	    public List<Products> getRecentProducts() {
	        return productsRepository.findRecentProducts();
	    }

	    public List<Products> getMostSoldProducts() {
	        return productsRepository.findMostSoldProducts();
	    }

	    public Double getTotalSalesBetweenDates(LocalDate startDate, LocalDate endDate) {
	        return productsRepository.findTotalSalesBetweenDates(startDate, endDate);
	    }

	    @Transactional
	    public List<SalesDTO> getSalesBetweenDates(LocalDate startDate, LocalDate endDate) {
	        List<Sales> sales = productsRepository.findSalesBetweenDates(startDate, endDate);

	        // Map Sales objects to SalesDTO objects containing productName and productPrice
	        List<SalesDTO> salesDTOs = sales.stream()
	                .map(sale -> new SalesDTO(
	                        sale.getProduct().getProductName(),
	                        sale.getProduct().getPrice(),
	                        sale.getQuantity(),
	                        sale.getTotalPrice(),
	                        sale.getSaleDate()))
	                .collect(Collectors.toList());

	        return salesDTOs;
	    }
	    
	    @Transactional
	    public ComparisonResponse compareProductsAndSales(LocalDate startDate, LocalDate endDate) {
	        List<Products> products = productsRepository.findAll();
	        List<ComparisonResult> results = new ArrayList<>();

	        LocalDateTime startDateTime = startDate.atStartOfDay();
	        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

	        double grandAddTotalPrice = 0;
	        double grandSoldPrice = 0;

	        for (Products product : products) {
	            int totalAddedQuantity = 0;
	            double totalAddedPrice = 0;
	            int totalSoldQuantity = 0;
	            double totalSoldPrice = 0;

	            LocalDateTime createdAt = product.getCreatedAt();
	            if (createdAt != null && createdAt.isAfter(startDateTime) && createdAt.isBefore(endDateTime)) {
	                totalAddedQuantity += product.getQuantity();
	                totalAddedPrice += product.getPrice() * product.getQuantity();
	                grandAddTotalPrice += totalAddedPrice; // Accumulate grand total for added prices
	            }

	            List<Sales> sales = salesRepository.findByProductAndSaleDateBetween(product, startDate, endDate);
	            for (Sales sale : sales) {
	                totalSoldQuantity += sale.getQuantity();
	                totalSoldPrice += sale.getTotalPrice();
	            }

	            grandSoldPrice += totalSoldPrice; 
	            
	            // Only add products to results if they have non-zero added or sold quantities/prices
	            if (totalAddedQuantity > 0 || totalAddedPrice > 0 || totalSoldQuantity > 0 || totalSoldPrice > 0) {
	                ComparisonResult comparisonResult = new ComparisonResult(
	                        product.getProductName(), totalAddedQuantity, totalAddedPrice, totalSoldQuantity, totalSoldPrice);
	                results.add(comparisonResult);
	            }

	        }

	        // Return a custom response object
	        return new ComparisonResponse(results, grandAddTotalPrice, grandSoldPrice);
	    }
}
