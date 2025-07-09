package com.spring.emailscheduler.controller;


// This contains all the endpoints needed to perform CRUD on bills

import com.spring.emailscheduler.dto.BillCreateRequest;
import com.spring.emailscheduler.dto.BillUpdateRequest;
import com.spring.emailscheduler.model.Bill;
import com.spring.emailscheduler.service.BillService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/bills")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class BillController {

    // Getting the Service Layer autowired
    @Autowired
    private BillService billService;

    // Get all the bills
    @GetMapping("/")
    public ResponseEntity<List<Bill>> getAllBills() {

        // Getting all the bills using the Service Layer
        List<Bill> bills = billService.getAllBills();
        return new ResponseEntity<>(bills, HttpStatus.OK);
    }

    // Get Bill By ID
    @GetMapping("/id/{id}")
    public ResponseEntity<Bill> getBillById(@PathVariable Long id) {

        Bill bill = billService.getBillById(id);
        return new ResponseEntity<>(bill, HttpStatus.OK);
    }

    // Get Bill using User Id
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Bill>> getAllBillsByUserId(@PathVariable Long userId) {

        List<Bill> bills = billService.getBillsByUserId(userId);
        return new ResponseEntity<>(bills, HttpStatus.OK);

    }

    // Get Bills within a particular date range for a user
    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<Bill>> getBillsInDateRange(@PathVariable Long userId,
                                                          @RequestParam String startDate,
                                                          @RequestParam String endDate) {

        // Converting Start date and end Date into Strings
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        List<Bill> bills = billService.getBillsInDateRange(userId, start, end);
        return new ResponseEntity<>(bills, HttpStatus.OK);
    }


    // Get the overdue bills
    @GetMapping("/overdue")
    public ResponseEntity<List<Bill>> getOverDueBills() {

        List<Bill> overdueBills = billService.getOverdueBills();
        return new ResponseEntity<>(overdueBills, HttpStatus.OK);

    }

    // Getting the categories of bill for a particular user
    @GetMapping("/user/{userId}/categories")
    public ResponseEntity<String> getCategoriesByUserId(@PathVariable Long userId) {
            List<String> categories = billService.getCategoriesByUserId(userId);
            return new ResponseEntity<>(String.valueOf(categories), HttpStatus.OK);
    }


    // Creating a new Bill
    @PostMapping
    public ResponseEntity<Bill> createBill(@Valid @RequestBody BillCreateRequest bill) {
        Bill createdBill = billService.createBill(bill);
        return new ResponseEntity<>(createdBill, HttpStatus.CREATED);
    }


    // Updating the Bill
    @PutMapping("/{id}")
    public ResponseEntity<Bill> updateBill(@PathVariable Long id, @Valid @RequestBody BillUpdateRequest bill) {

        Bill updatedBill = billService.updateBill(id, bill);
        return new ResponseEntity<>(updatedBill, HttpStatus.OK);
    }

    // Endpoint to toggle the Bill Status to paid
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<Bill> toggleBillStatus(@PathVariable Long id) {
        Bill updatedBill = billService.toggleBill(id);
        return new ResponseEntity<>(updatedBill, HttpStatus.OK);
    }

    // Deleting a bill
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBill(@PathVariable Long id) {
        String Message = billService.deleteBill(id);
        return new ResponseEntity<>(Message, HttpStatus.OK);

    }

}
