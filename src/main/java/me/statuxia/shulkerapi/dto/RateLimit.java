package me.statuxia.shulkerapi.dto;

import me.statuxia.shulkerapi.model.TokenLimitation;

public class RateLimit {

    protected Long remaining;
    protected Long limit;
    protected Long reset;

    public RateLimit(RateLimit source) {
        this.remaining = source.remaining;
        this.limit = source.limit;
        this.reset = source.reset;
    }

    public RateLimit(TokenLimitation tokenLimitation) {
        this.remaining = tokenLimitation.getRateLimit();
        this.limit = tokenLimitation.getRateLimit();
        this.reset = System.nanoTime() + tokenLimitation.getRateResetSeconds() * 1_000_000_000;
    }

    public void decrement() {
        if (remaining > 0) {
            remaining--;
        }
    }

    public void update(TokenLimitation tokenLimitation) {
        this.remaining = tokenLimitation.getRateLimit();
        this.limit = tokenLimitation.getRateLimit();
        this.reset = System.nanoTime() + tokenLimitation.getRateResetSeconds() * 1_000_000_000;
    }

    public Long getRemaining() {
        return remaining;
    }

    public Long getLimit() {
        return limit;
    }

    public Long getReset() {
        return reset;
    }
}
