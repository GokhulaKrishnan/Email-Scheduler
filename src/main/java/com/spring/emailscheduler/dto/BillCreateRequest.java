package com.spring.emailscheduler.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillCreateRequest {

    @NotBlank(message = "Bill name is required")
    @Size(min = 2, max = 200, message = "Bill name must be between 2 and 200 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    @NotBlank(message = "Frequency is required")
    private String frequency;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "User ID is required")
    private Long userId;

    private boolean autoPay = false;
}
