package me.statuxia.shulkerapi.processor.impl.card;

import jakarta.annotation.PostConstruct;
import me.statuxia.shulkerapi.dto.operation.OperationData;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.service.BankCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Обработка операций, связанных с переводом средств
 */
@Component
public class TransferFundsProcessor extends BaseProcessor {

    public static final String VALUE = "value";
    public static final String CARD = "card";
    public static final String RECEIVER = "receiver";
    public static final String MESSAGE = "message";

    private final HashMap<String, Function<Object, Boolean>> map = new HashMap<>();

    private final BankCardService bankCardService;

    @Autowired
    public TransferFundsProcessor(BankCardService bankCardService) {
        this.bankCardService = bankCardService;
    }

    @PostConstruct
    public void init() {
        map.put(VALUE, Long.class::isInstance);
        map.put(CARD, BankCard.class::isInstance);
        map.put(RECEIVER, BankCard.class::isInstance);
        map.put(MESSAGE, String.class::isInstance);
    }

    @Override
    protected Map<String, Function<Object, Boolean>> getValidators() {
        return map;
    }

    @Override
    @Transactional
    public void process(OperationData data) {
        final Long value = (Long) data.getData().get(VALUE);
        final BankCard card = (BankCard) data.getData().get(CARD);
        final BankCard receiver = (BankCard) data.getData().get(RECEIVER);
        final String message = (String) data.getData().get(MESSAGE);

        bankCardService.transferFunds(card, receiver, value, message);
    }
}
