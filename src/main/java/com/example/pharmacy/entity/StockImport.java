package com.example.pharmacy.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "stock_imports")
public class StockImport {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false)
    private Medicine medicine;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false)
    private Instant importedAt;

    protected StockImport() {}

    public StockImport(Medicine medicine, int quantity) {
        this.medicine = medicine;
        this.quantity = quantity;
        this.importedAt = Instant.now();
    }

    public Long getId() { return id; }
    public int getQuantity() { return quantity; }
}
