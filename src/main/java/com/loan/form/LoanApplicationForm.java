package com.loan.form;

import java.math.BigDecimal;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

public class LoanApplicationForm {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Enter a valid email")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "500.00", message = "Minimum amount is 500.00")
    private BigDecimal amount;

    // --- New fields for document uploads ---
    @NotNull(message = "Pay stub is required")
    private MultipartFile payStub;

    @NotNull(message = "Bank statement is required")
    private MultipartFile bankStatement;



    // --- Getters & Setters ---
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public MultipartFile getPayStub() { return payStub; }
    public void setPayStub(MultipartFile payStub) { this.payStub = payStub; }

    public MultipartFile getBankStatement() { return bankStatement; }
    public void setBankStatement(MultipartFile bankStatement) { this.bankStatement = bankStatement; }

}
