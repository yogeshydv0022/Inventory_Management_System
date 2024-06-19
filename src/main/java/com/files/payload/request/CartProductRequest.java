package com.files.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartProductRequest {
    private Long productId;
    private int quantity;

    // Getters and Setters
}
