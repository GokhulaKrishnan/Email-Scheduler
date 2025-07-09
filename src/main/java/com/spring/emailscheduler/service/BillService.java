package com.spring.emailscheduler.service;

import com.spring.emailscheduler.dto.BillCreateRequest;
import com.spring.emailscheduler.dto.BillUpdateRequest;
import com.spring.emailscheduler.model.Bill;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

public interface BillService {
    List<Bill> getAllBills();

    Bill getBillById(Long id);

    List<Bill> getBillsByUserId(Long userId);

    List<Bill> getBillsInDateRange(Long userId, LocalDate start, LocalDate end);

    List<Bill> getOverdueBills();

    List<String> getCategoriesByUserId(Long userId);

    Bill createBill(@Valid BillCreateRequest bill);

    Bill updateBill(Long id, @Valid BillUpdateRequest bill);

    Bill toggleBill(Long id);

    @Transactional
    String deleteBill(Long id);
}
