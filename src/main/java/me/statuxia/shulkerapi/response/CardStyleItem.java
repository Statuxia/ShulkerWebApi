package me.statuxia.shulkerapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import me.statuxia.shulkerapi.model.CardType;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardStyleItem {

    private String name;
    private String value;
    private CardType cardtype;
    private Long price;

    public CardStyleItem() {
    }

    public CardStyleItem(String name, String value, CardType cardtype, Long price) {
        this.name = name;
        this.value = value;
        this.cardtype = cardtype;
        if (price >= 0) {
            this.price = price;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public CardType getCardtype() {
        return cardtype;
    }

    public void setCardtype(CardType cardtype) {
        this.cardtype = cardtype;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }
}
