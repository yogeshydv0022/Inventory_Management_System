package com.files.payload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ComparisonResponse {
    private List<ComparisonResult> products;
    private double grandAddTotalPrice;
    private double grandSoldPrice;
}