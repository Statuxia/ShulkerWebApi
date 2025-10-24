package me.statuxia.shulkerapi.utils;

import me.statuxia.shulkerapi.model.*;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;

import java.util.UUID;

public class CardHistoryUtils {

    private CardHistoryUtils() {
    }

    public static BankCardHistory build(BankCard card, BankCardHistoryType type) {
        final BankCardHistory history = new BankCardHistory();
        history.setCreateTime(DateTime.now());
        history.setCard(card);
        history.setType(type);
        return history;
    }

    public static BankCardHistory copy(BankCardHistory from) {
        final BankCardHistory to = new BankCardHistory();
        BeanUtils.copyProperties(from, to);
        return to;
    }

    public static BankCardOperationHistory build(BankCardHistory history, Long value) {
        final BankCardOperationHistory operation = new BankCardOperationHistory();
        operation.setCard(history.getCard());
        operation.setUuid(history.getUuid());
        operation.setState(BankOperationState.EXISTS);
        operation.setCreateTime(DateTime.now());
        operation.setValue(value);
        return operation;
    }

    public static BankCardOperationHistory copy(BankCardOperationHistory from) {
        final BankCardOperationHistory to = new BankCardOperationHistory();
        BeanUtils.copyProperties(from, to);
        return to;
    }

    public static BankCardLog build(BankCard card, String actionBy, UUID uuid) {
        final BankCardLog log = new BankCardLog();
        log.setCard(card);
        log.setUuid(uuid);
        log.setActionBy(actionBy);
        log.setActionTime(DateTime.now());
        return log;
    }

    public static BankCardLog copy(BankCardLog from) {
        final BankCardLog to = new BankCardLog();
        BeanUtils.copyProperties(from, to);
        return to;
    }
}
