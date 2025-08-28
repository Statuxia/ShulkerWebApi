package me.statuxia.shulkerapi.processor;

import me.statuxia.shulkerapi.dto.operation.OperationData;

public interface OperationProcessor {

    boolean isSuitable(OperationData data);

    void validate(OperationData data);

    void process(OperationData data);
}
