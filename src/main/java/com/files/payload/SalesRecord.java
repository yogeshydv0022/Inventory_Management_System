package com.files.payload;

import java.time.LocalDate;

import com.files.model.Products;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesRecord {
	
	    private LocalDate date;
	    private Products product;
	    private int quantity;
	    private double totalPrice;

}
