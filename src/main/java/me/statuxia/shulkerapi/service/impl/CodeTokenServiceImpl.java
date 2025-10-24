package me.statuxia.shulkerapi.service.impl;

import me.statuxia.shulkerapi.dao.SessionTokenDAO;
import me.statuxia.shulkerapi.dto.CodeToken;
import me.statuxia.shulkerapi.model.SessionToken;
import me.statuxia.shulkerapi.service.CodeTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class CodeTokenServiceImpl implements CodeTokenService {

    private final SessionTokenDAO sessionTokenDAO;
    private final Map<String, CodeToken> codeTokens = new HashMap<>();

    @Autowired
    public CodeTokenServiceImpl(SessionTokenDAO sessionTokenDAO) {
        this.sessionTokenDAO = sessionTokenDAO;
    }

    @Override
    public void addCodeToken(String token, String code) {
        codeTokens.put(code, new CodeToken(code, token));
    }

    @Override
    public Optional<SessionToken> getByCode(String code) {
        final CodeToken codeToken = codeTokens.remove(code);
        if (codeToken == null) {
            return Optional.empty();
        }

        return sessionTokenDAO.findByToken(codeToken.getToken());
    }

    @Scheduled(initialDelay = 25L, fixedRate = 60, timeUnit = TimeUnit.SECONDS)
    public void process() {
        final List<String> burnCodes = new ArrayList<>();
        codeTokens.forEach((key, value) -> {
            if (value.getBurnTime() < System.nanoTime()) {
                burnCodes.add(key);
            }
        });

        burnCodes.forEach(codeTokens::remove);
    }
}
