package com.files.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_bill")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductBill {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    private String productName;
	    private double price;
	    private int quantity;
	    private double subtotal;


	    // Constructor with parameters
	    public ProductBill(String productName, double price, int quantity, double subtotal) {
	        this.productName = productName;
	        this.price = price;
	        this.quantity = quantity;
	        this.subtotal = subtotal;
	    }
}