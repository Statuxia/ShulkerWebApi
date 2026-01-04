package me.statuxia.shulkerapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import me.statuxia.shulkerapi.model.GameSessionIpState;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthValidateResponse {

    protected GameSessionIpState state;

    public GameSessionIpState getState() {
        return state;
    }

    public AuthValidateResponse setState(GameSessionIpState state) {
        this.state = state;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AuthValidateResponse response = (AuthValidateResponse) o;
        return state == response.state;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(state);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AuthValidateResponse{");
        sb.append("state=").append(state);
        sb.append('}');
        return sb.toString();
    }
}
