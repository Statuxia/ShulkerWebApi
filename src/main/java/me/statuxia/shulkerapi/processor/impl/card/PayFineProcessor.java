package me.statuxia.shulkerapi.processor.impl.card;

import jakarta.annotation.PostConstruct;
import me.statuxia.shulkerapi.dto.operation.OperationData;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.Fine;
import me.statuxia.shulkerapi.service.BankCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Обработка операций, связанных с оплатой штрафов
 */
@Component
public class PayFineProcessor extends BaseProcessor {
    public static final String VALUE = "value";
    public static final String CARD = "card";
    public static final String FINE = "fine";

    private final HashMap<String, Function<Object, Boolean>> map = new HashMap<>();

    private final BankCardService bankCardService;

    @Autowired
    public PayFineProcessor(BankCardService bankCardService) {
        this.bankCardService = bankCardService;
    }

    @PostConstruct
    public void init() {
        map.put(VALUE, Long.class::isInstance);
        map.put(CARD, BankCard.class::isInstance);
        map.put(FINE, Fine.class::isInstance);
    }

    @Override
    protected Map<String, Function<Object, Boolean>> getValidators() {
        return map;
    }

    @Override
    public void process(OperationData data) {
        final Long value = (Long) data.getData().get(VALUE);
        final BankCard card = (BankCard) data.getData().get(CARD);
        final Fine fine = (Fine) data.getData().get(FINE);

        bankCardService.payFine(card, value, fine);
    }
}
