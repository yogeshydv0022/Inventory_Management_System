package com.files.payload;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesDTO {

    private String productName;
    private double productPrice;
    private int quantity;
    private double totalPrice;
    private LocalDate saleDate;
}
