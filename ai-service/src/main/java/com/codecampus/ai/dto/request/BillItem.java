package com.codecampus.ai.dto.request;

public record BillItem(
        String itemName,
        String unit,
        Integer quantity,
        Double price,
        Double subTotal
) {
}
