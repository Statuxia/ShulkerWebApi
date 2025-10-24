package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferFundsRequest extends BaseCardRequest {

    @Pattern(regexp = "\\d{4} \\d{4}")
    protected String receiverCard;

    @Pattern(regexp = "\\d{4}")
    private String pin;

    @Min(1)
    private Long funds;

    @Length(max = 500)
    private String message;

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public Long getFunds() {
        return funds;
    }

    public void setFunds(Long funds) {
        this.funds = funds;
    }

    public String getReceiverCard() {
        return receiverCard;
    }

    public void setReceiverCard(String receiverCard) {
        this.receiverCard = receiverCard;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
