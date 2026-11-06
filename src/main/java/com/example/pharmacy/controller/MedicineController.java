package com.example.pharmacy.controller;

import com.example.pharmacy.dto.MedicineRequest;
import com.example.pharmacy.dto.MedicineResponse;
import com.example.pharmacy.dto.MedicineUpdateCommand;
import com.example.pharmacy.dto.StockImportRequest;
import com.example.pharmacy.dto.SaleResponse;
import com.example.pharmacy.service.MedicineService;
import com.example.pharmacy.service.MedicineSaleService;
import com.example.pharmacy.service.StockImportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/medicines")
public class MedicineController {
    private final MedicineService medicineService;
    private final StockImportService stockImportService;
    private final MedicineSaleService saleService;

    public MedicineController(MedicineService medicineService, StockImportService stockImportService,
                              MedicineSaleService saleService) {
        this.medicineService = medicineService;
        this.stockImportService = stockImportService;
        this.saleService = saleService;
    }

    @GetMapping("/{id}")
    public MedicineResponse getById(@PathVariable Long id) {
        return medicineService.getById(id);
    }

    @PostMapping
    public ResponseEntity<MedicineResponse> create(@Valid @RequestBody MedicineRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(medicineService.create(request));
    }

    @PutMapping("/{id}")
    public MedicineResponse update(@PathVariable Long id, @Valid @RequestBody MedicineRequest request) {
        return medicineService.update(new MedicineUpdateCommand(id, request.name(), request.price()));
    }

    @PostMapping("/{id}/imports")
    public ResponseEntity<Void> importStock(@PathVariable Long id, @Valid @RequestBody StockImportRequest request) {
        stockImportService.importStock(id, request.quantity());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{id}/sell")
    public SaleResponse sellMedicine(@PathVariable Long id) {
        return new SaleResponse(id, saleService.sellMedicine(id));
    }
}
