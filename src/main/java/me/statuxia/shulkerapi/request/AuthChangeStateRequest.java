package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import me.statuxia.shulkerapi.model.GameSessionIpState;
import org.springframework.lang.NonNull;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthChangeStateRequest {

    @NonNull
    protected Long id;
    protected GameSessionIpState state;

    @NonNull
    public Long getId() {
        return id;
    }

    public AuthChangeStateRequest setId(@NonNull Long id) {
        this.id = id;
        return this;
    }

    public GameSessionIpState getState() {
        return state;
    }

    public AuthChangeStateRequest setState(GameSessionIpState state) {
        this.state = state;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AuthChangeStateRequest that = (AuthChangeStateRequest) o;
        return Objects.equals(id, that.id) && state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, state);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AuthChangeStateRequest{");
        sb.append("id=").append(id);
        sb.append(", state=").append(state);
        sb.append('}');
        return sb.toString();
    }
}
