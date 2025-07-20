package com.codecampus.ai.dto.request;

public record ExpenseInfo(
        String category,
        String itemName,
        Double amount
) {
}
