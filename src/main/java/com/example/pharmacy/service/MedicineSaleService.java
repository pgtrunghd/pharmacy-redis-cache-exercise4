package com.example.pharmacy.service;

import com.example.pharmacy.entity.Medicine;
import com.example.pharmacy.repository.MedicineRepository;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MedicineSaleService {
    private final RedissonClient redisson;
    private final MedicineRepository medicines;
    private final TransactionTemplate transactions;

    public MedicineSaleService(RedissonClient redisson, MedicineRepository medicines,
                               TransactionTemplate transactions) {
        this.redisson = redisson;
        this.medicines = medicines;
        this.transactions = transactions;
    }

    @CacheEvict(value = "medicines", key = "#id")
    public int sellMedicine(Long id) {
        RLock lock = redisson.getLock("lock:medicine:" + id);
        boolean acquired;
        try {
            acquired = lock.tryLock(3, 5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Interrupted while waiting for medicine lock", e);
        }
        if (!acquired) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Medicine is busy; retry the sale");
        }
        try {
            return transactions.execute(status -> {
                Medicine medicine = medicines.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medicine not found"));
                if (!medicine.sellOne()) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Medicine is out of stock");
                }
                medicines.saveAndFlush(medicine);
                return medicine.getStockQuantity();
            });
        } finally {
            if (lock.isHeldByCurrentThread()) lock.unlock();
        }
    }
}
