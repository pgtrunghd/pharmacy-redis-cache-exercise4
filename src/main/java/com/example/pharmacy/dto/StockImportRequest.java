package com.example.pharmacy.dto;

import jakarta.validation.constraints.Min;

public record StockImportRequest(@Min(1) int quantity) {}
