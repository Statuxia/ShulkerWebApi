package me.statuxia.shulkerapi.processor.impl.card;

import jakarta.annotation.PostConstruct;
import me.statuxia.shulkerapi.dto.operation.OperationData;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.CardOperationType;
import me.statuxia.shulkerapi.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Обработка операций, связанных с пополнением или снятием средств
 * Не используется для переводов
 */
@Component
public class BalanceProcessor extends BaseProcessor {

    public static final String OPERATION = "operation";
    public static final String VALUE = "value";
    public static final String CARD = "card";

    private final HashMap<String, Function<Object, Boolean>> map = new HashMap<>();

    private final CardService cardService;

    @Autowired
    public BalanceProcessor(CardService cardService) {
        this.cardService = cardService;
    }

    @PostConstruct
    public void init() {
        map.put(OPERATION, CardOperationType.class::isInstance);
        map.put(VALUE, Long.class::isInstance);
        map.put(CARD, BankCard.class::isInstance);
    }

    @Override
    protected Map<String, Function<Object, Boolean>> getValidators() {
        return map;
    }

    @Override
    @Transactional
    public void process(OperationData data) {
        final CardOperationType operation = (CardOperationType) data.getData().get(OPERATION);
        final Long value = (Long) data.getData().get(VALUE);
        final BankCard card = (BankCard) data.getData().get(CARD);

        if (value < 0) {
            cardService.withdrawFunds(card, Math.abs(value));
            return;
        }

        if (value > 0) {
            cardService.depositFunds(card, value);
        }
    }
}
