package com.example.pharmacy.service;

import com.example.pharmacy.dto.PharmacyAlert;
import com.example.pharmacy.entity.Medicine;
import com.example.pharmacy.entity.StockImport;
import com.example.pharmacy.repository.MedicineRepository;
import com.example.pharmacy.repository.StockImportRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.cache.annotation.CacheEvict;

@Service
public class StockImportService {
    private final MedicineRepository medicines;
    private final StockImportRepository imports;
    private final PharmacyAlertPublisher publisher;
    private final TransactionTemplate transactions;

    public StockImportService(MedicineRepository medicines, StockImportRepository imports,
                              PharmacyAlertPublisher publisher, TransactionTemplate transactions) {
        this.medicines = medicines;
        this.imports = imports;
        this.publisher = publisher;
        this.transactions = transactions;
    }

    @CacheEvict(value = "medicines", key = "#medicineId")
    public StockImport importStock(Long medicineId, int quantity) {
        String[] medicineName = new String[1];
        StockImport saved = transactions.execute(status -> {
            Medicine medicine = medicines.findById(medicineId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicine not found"));
            medicine.addStock(quantity);
            medicines.save(medicine);
            medicineName[0] = medicine.getName();
            return imports.save(new StockImport(medicine, quantity));
        });
        publisher.publish(new PharmacyAlert("IMPORT", "Đã nhập " + quantity + " hộp " + medicineName[0]));
        return saved;
    }
}
