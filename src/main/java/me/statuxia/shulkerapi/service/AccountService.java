package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.model.GameAccount;
import me.statuxia.shulkerapi.model.TokenAuthorityEnum;
import me.statuxia.shulkerapi.request.CardCreateRequest;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {

    GameAccount getOwner(
        TokenData token,
        CardCreateRequest request,
        TokenAuthorityEnum authorityForSkip
    );

    void validateOwned(
        TokenData token,
        GameAccount account
    );
}
