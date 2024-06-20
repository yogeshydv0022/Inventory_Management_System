package com.files.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.files.model.Products;
import com.files.payload.ComparisonResponse;
import com.files.payload.SalesDTO;
import com.files.services.SalesService;

@RestController
@RequestMapping("/ism/sales")
public class SalesController {

	@Autowired
	private SalesService productsService;

	@GetMapping("/")
	public List<Products> getRecentProducts() {
		return productsService.getRecentProducts();
	}

	@GetMapping("/ms")
	public List<Products> getMostSoldProducts() {
		return productsService.getMostSoldProducts();
	}

	@GetMapping("/s")
    public ResponseEntity<List<SalesDTO>> getSalesBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<SalesDTO> sales = productsService.getSalesBetweenDates(startDate, endDate);
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }

	@GetMapping("/ts")
	public Double getTotalSalesBetweenDates(@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate) {
		return productsService.getTotalSalesBetweenDates(LocalDate.parse(startDate), LocalDate.parse(endDate));
	}

	@GetMapping("/pcs")

    public ResponseEntity<ComparisonResponse> compareProductsAndSales(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {

        ComparisonResponse comparisonResponse = productsService.compareProductsAndSales(startDate, endDate);
        return ResponseEntity.ok(comparisonResponse);
    }
}