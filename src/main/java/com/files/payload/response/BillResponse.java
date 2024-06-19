package com.files.payload.response;
import java.time.LocalDateTime;
import java.util.List;

import com.files.model.ProductBill;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BillResponse {
    private List<ProductBill> products;
    private double grandTotal;
    private LocalDateTime generatedAt;
    private Long billId;
    private boolean paymentStatus;
}
