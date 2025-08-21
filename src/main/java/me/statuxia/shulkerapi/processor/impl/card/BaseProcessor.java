package me.statuxia.shulkerapi.processor.impl.card;

import me.statuxia.shulkerapi.dto.operation.OperationData;
import me.statuxia.shulkerapi.exception.BaseApiException;
import me.statuxia.shulkerapi.processor.CardProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Function;

public abstract class BaseProcessor implements CardProcessor {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected abstract Map<String, Function<Object, Boolean>> getValidators();

    @Override
    public boolean isSuitable(OperationData data) {
        return data.getProcessorChain().contains(this.getClass());
    }

    @Override
    public void validate(OperationData data) {
        if (data == null) {
            warn("data arg", data);
            throw BaseApiException.INCORRECT_DATA;
        }

        if (!getValidators().entrySet().stream().allMatch(entry -> {
            final Object value = data.getData().get(entry.getKey());
            final Boolean result = entry.getValue().apply(value);
            if (Boolean.FALSE.equals(result)) {
                warn(entry.getKey(), value);
                return false;
            }

            return true;
        })) {
            throw BaseApiException.INCORRECT_DATA;
        }
    }

    private void warn(String key, Object operation) {
        logger.warn(
            "[{}] wrong key, value: {}, class: {}",
            key, operation, operation == null ? null : operation.getClass()
        );
    }
}
