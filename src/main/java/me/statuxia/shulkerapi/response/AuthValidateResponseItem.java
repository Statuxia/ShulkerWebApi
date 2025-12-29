package me.statuxia.shulkerapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import me.statuxia.shulkerapi.model.GameSessionIpState;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthValidateResponseItem {

    protected String username;
    protected GameSessionIpState state;

    public GameSessionIpState getState() {
        return state;
    }

    public AuthValidateResponseItem setState(GameSessionIpState state) {
        this.state = state;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public AuthValidateResponseItem setUsername(String username) {
        this.username = username;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AuthValidateResponseItem that = (AuthValidateResponseItem) o;
        return state == that.state && Objects.equals(getUsername(), that.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(state);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AuthValidateResponseItem{");
        sb.append("state=").append(state);
        sb.append("username=").append(username);
        sb.append('}');
        return sb.toString();
    }
}
