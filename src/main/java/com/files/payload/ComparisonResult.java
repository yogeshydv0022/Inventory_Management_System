package com.files.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComparisonResult {
	
	
    private String productName;
    private int totalAddedQuantity;
    private double totalAddedPrice;
    private int totalSoldQuantity;
    private double totalSoldPrice;
    
}