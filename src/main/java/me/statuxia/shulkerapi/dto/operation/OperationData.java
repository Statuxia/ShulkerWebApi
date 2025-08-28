package me.statuxia.shulkerapi.dto.operation;

import me.statuxia.shulkerapi.processor.OperationProcessor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OperationData {

    private final Map<String, Object> data = new HashMap<>();
    private final Set<Class<? extends OperationProcessor>> processorChain = new HashSet<>();

    public Map<String, Object> getData() {
        return data;
    }

    public OperationData setData(Map<String, Object> data) {
        this.data.clear();

        if (data == null) {
            return this;
        }

        this.data.putAll(data);
        return this;
    }

    public OperationData addData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    public Set<Class<? extends OperationProcessor>> getProcessorChain() {
        return processorChain;
    }

    public OperationData addProcessor(Class<? extends OperationProcessor> processor) {
        processorChain.add(processor);
        return this;
    }

    public OperationData setProcessorChain(Set<Class<? extends OperationProcessor>> processorChain) {
        this.processorChain.clear();

        if (processorChain == null) {
            return this;
        }

        this.processorChain.addAll(processorChain);
        return this;
    }
}
