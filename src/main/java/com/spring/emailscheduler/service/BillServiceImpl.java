package com.spring.emailscheduler.service;

import com.spring.emailscheduler.dto.BillCreateRequest;
import com.spring.emailscheduler.dto.BillUpdateRequest;
import com.spring.emailscheduler.exceptions.APIExceptionHandler;
import com.spring.emailscheduler.model.Bill;
import com.spring.emailscheduler.model.PaymentFrequency;
import com.spring.emailscheduler.model.User;
import com.spring.emailscheduler.repositories.BillReminderRepository;
import com.spring.emailscheduler.repositories.BillRepository;
import com.spring.emailscheduler.repositories.UserRespository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BillServiceImpl implements BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private UserRespository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private BillReminderRepository billReminderRepository;
    @Autowired
    private BillReminderService billReminderService;
    @Autowired
    private ModelMapper modelMapper;


    @Override
    public List<Bill> getAllBills() {

        List<Bill> bills = billRepository.findAll();

        // Throwing an API excpetion when there are no bills availabel
        if (bills.isEmpty()) {
            throw new APIExceptionHandler("There are no bills");
        }
        return bills;
    }

    @Override
    public Bill getBillById(Long id) {

        Bill bill = billRepository.findById(id).orElseThrow(() -> new APIExceptionHandler("Bill not found"));

        if (bill == null) {
            throw new APIExceptionHandler("Bill not found");
        }
        return bill;
    }

    // Getting Bill using User Id
    @Override
    public List<Bill> getBillsByUserId(Long userId) {

        // Here we are getting only the bills which are active
        List<Bill> userBills  = billRepository.findByUserIdAndIsActiveTrue(userId);
        return userBills;
    }


    // Getting the user bills within a particular date range
    @Override
    public List<Bill> getBillsInDateRange(Long userId, LocalDate start, LocalDate end) {

        // Using Custom query in repository getting all the bills with the range
        List<Bill> bills = billRepository.findBillsInDateRange(userId, start, end);

        // If List is empty then throwing exception
        if (bills.isEmpty()) {
            throw new APIExceptionHandler("There are no bills within the given date range");
        }
        return bills;
    }

    // Getting all the overdue bills
    @Override
    public List<Bill> getOverdueBills() {
        List<Bill> bills = billRepository.findOverDueBills(LocalDate.now());

        // If no bills
        if (bills.isEmpty()) {
            throw new APIExceptionHandler("There are no overdue bills");
        }
        return bills;
    }

    @Override
    public List<String> getCategoriesByUserId(Long userId) {

        List<String> categories = billRepository.findCategoriesByUserId(userId);

        if (categories.isEmpty()) {
            throw new APIExceptionHandler("There are no categories");
        }

        return categories;
    }

    // Creating a new bill
    @Override
    public Bill createBill(BillCreateRequest bill) {

        // Getting the user from the give bill from the repo
        User user = userRepository.findById(bill.getUserId()).
                orElseThrow(() -> new APIExceptionHandler("User not found"));

        // Creating a new instance of the Bill
        Bill newBill = new Bill();

        newBill.setUser(user);
        newBill.setAmount(bill.getAmount());
        newBill.setDescription(bill.getDescription());
        newBill.setName(bill.getName());
        newBill.setDueDate(bill.getDueDate());
        newBill.setCategory(bill.getCategory());

        // In Bill CreateRequest we have frequency in string, we need to get the enum and assign it here
        PaymentFrequency frequency = PaymentFrequency.valueOf(bill.getFrequency().toUpperCase());
        newBill.setFrequency(frequency);


        // Saving the bill
        Bill savedBill = billRepository.save(newBill);

        // Create reminders for the new bill
        billReminderService.createRemindersForBill(savedBill);

        // 2. We also need to add the new bill to the particular user bill list.
        // Adding the new bill to the user
        user.getBills().add(savedBill);


        return savedBill;
    }

    // Updating the existing bill
    @Override
    public Bill updateBill(Long id, BillUpdateRequest billUpdateRequest) {

        // Getting the bill from the db
        Bill billToBeUpdated = billRepository.findById(id).orElseThrow(() -> new APIExceptionHandler("Bill not found"));

        // Converting Dto to entity
        Bill bill = modelMapper.map(billUpdateRequest, Bill.class);


        billToBeUpdated.setAmount(bill.getAmount());
        billToBeUpdated.setName(bill.getName());
        billToBeUpdated.setDescription(bill.getDescription());
        billToBeUpdated.setDueDate(bill.getDueDate());
        billToBeUpdated.setFrequency(bill.getFrequency());
        billToBeUpdated.setCategory(bill.getCategory());
        billToBeUpdated.setActive(bill.isActive());
        billToBeUpdated.setAutoPay(bill.isAutoPay());
        Bill updatedBill = billRepository.save(billToBeUpdated);

        // We also need to update the reminders for the updated Bill
        billReminderService.updateRemindersForBill(updatedBill);
        return updatedBill;
    }

    // To change the status of the bill
    @Override
    public Bill toggleBill(Long id) {

        // Get the existing bill

        Bill bill = billRepository.findById(id).orElseThrow(() -> new APIExceptionHandler("Bill not found"));
        // Set the active status to not active
        bill.setActive(!bill.isActive());

        Bill updatedBill = billRepository.save(bill);
        return updatedBill;
    }

    // Deleting a bill
    @Transactional
    @Override
    public String deleteBill(Long id) {

        // Check if the bill exists
        Bill bill = billRepository.findById(id).orElseThrow(() -> new APIExceptionHandler("Bill not found"));

        // First we need to remove the bill from the User Bill List
        User user = bill.getUser();
        if (user != null) {
            user.getBills().remove(bill);
            userRepository.save(user);
        }

        // Delete associated reminders first (if not using CASCADE)
        billReminderRepository.deleteByBillId(id);

        billRepository.deleteById(id);
        return "Successfully deleted the bill";
    }


}
