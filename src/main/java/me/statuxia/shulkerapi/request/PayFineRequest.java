package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayFineRequest {

    @NotNull
    private Long id;

    @Pattern(regexp = "\\d{4} \\d{4}")
    private String paymentCardNumber;

    @Pattern(regexp = "\\d{4}")
    private String paymentCardPin;

    @NotNull
    @NotEmpty
    private String gameAccount;

    public Long getId() {
        return id;
    }

    public PayFineRequest setId(Long id) {
        this.id = id;
        return this;
    }

    public String getPaymentCardNumber() {
        return paymentCardNumber;
    }

    public PayFineRequest setPaymentCardNumber(String paymentCardNumber) {
        this.paymentCardNumber = paymentCardNumber;
        return this;
    }

    public String getPaymentCardPin() {
        return paymentCardPin;
    }

    public PayFineRequest setPaymentCardPin(String paymentCardPin) {
        this.paymentCardPin = paymentCardPin;
        return this;
    }

    public String getGameAccount() {
        return gameAccount;
    }

    public PayFineRequest setGameAccount(String gameAccount) {
        this.gameAccount = gameAccount;
        return this;
    }
}
