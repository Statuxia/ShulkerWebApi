package me.statuxia.shulkerapi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthRefreshResponseItem {

    protected String username;
    protected boolean success;
    protected String errorMessage;
    protected Integer errorCode;

    public String getUsername() {
        return username;
    }

    public AuthRefreshResponseItem setUsername(String username) {
        this.username = username;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public AuthRefreshResponseItem setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public AuthRefreshResponseItem setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public AuthRefreshResponseItem setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AuthRefreshResponseItem that = (AuthRefreshResponseItem) o;
        return success == that.success
            && Objects.equals(username, that.username)
            && Objects.equals(errorMessage, that.errorMessage)
            && Objects.equals(errorCode, that.errorCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, success, errorMessage, errorCode);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AuthRefreshResponseItem{");
        sb.append("username='").append(username).append('\'');
        sb.append(", success=").append(success);
        sb.append(", errorMessage='").append(errorMessage).append('\'');
        sb.append(", errorCode=").append(errorCode);
        sb.append('}');
        return sb.toString();
    }
}
