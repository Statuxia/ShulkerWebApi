package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.model.SessionToken;

import java.util.Optional;

public interface CodeTokenService {

    void addCodeToken(String token, String code);

    Optional<SessionToken> getByCode(String code);
}
