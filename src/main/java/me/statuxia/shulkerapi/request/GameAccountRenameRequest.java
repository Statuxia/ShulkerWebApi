package me.statuxia.shulkerapi.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.lang.NonNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameAccountRenameRequest {

    @NonNull
    @NotEmpty
    private String oldName;
    @NonNull
    @NotEmpty
    private String newName;

    @NonNull
    public String getOldName() {
        return oldName;
    }

    public void setOldName(@NonNull String oldName) {
        this.oldName = oldName;
    }

    @NonNull
    public String getNewName() {
        return newName;
    }

    public void setNewName(@NonNull String newName) {
        this.newName = newName;
    }
}
