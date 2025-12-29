package me.statuxia.shulkerapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthValidateResponse {

    protected List<AuthValidateResponseItem> items;

    public List<AuthValidateResponseItem> getItems() {
        return items;
    }

    public void setItems(List<AuthValidateResponseItem> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AuthValidateResponse that = (AuthValidateResponse) o;
        return Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(items);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AuthValidateResponse{");
        sb.append("items=").append(items);
        sb.append('}');
        return sb.toString();
    }
}
