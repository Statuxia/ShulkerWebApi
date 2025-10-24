package me.statuxia.shulkerapi.dto;

public class CodeToken {

    private final String code;
    private final String token;
    private final Long burnTime;

    public CodeToken(String code, String token) {
        this.code = code;
        this.token = token;
        this.burnTime = System.nanoTime() + 300L * 1_000_000_000;
    }

    public String getCode() {
        return code;
    }

    public String getToken() {
        return token;
    }

    public Long getBurnTime() {
        return burnTime;
    }
}
