package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MinigamesJoinServerRequest {

    @NotNull
    @Min(1)
    private Long gameAccountId;

    public Long getGameAccountId() {
        return gameAccountId;
    }

    public MinigamesJoinServerRequest setGameAccountId(Long gameAccountId) {
        this.gameAccountId = gameAccountId;
        return this;
    }
}
