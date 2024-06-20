package com.files.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrandTotalResult {
	
    private double grandAddTotalPrice;
    private double grandSoldPrice;

    
}