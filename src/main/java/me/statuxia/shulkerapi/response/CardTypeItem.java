package me.statuxia.shulkerapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardTypeItem {

    private String name;
    private String value;
    private Long price;

    public CardTypeItem() {
    }

    public CardTypeItem(String name, String value, Long price) {
        this.name = name;
        this.value = value;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public CardTypeItem setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public CardTypeItem setValue(String value) {
        this.value = value;
        return this;
    }

    public Long getPrice() {
        return price;
    }

    public CardTypeItem setPrice(Long price) {
        this.price = price;
        return this;
    }
}
