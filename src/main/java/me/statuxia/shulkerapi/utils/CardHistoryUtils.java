package me.statuxia.shulkerapi.utils;

import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.BankCardHistory;
import me.statuxia.shulkerapi.model.BankCardHistoryType;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;

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
}
