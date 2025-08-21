package me.statuxia.shulkerapi.service.impl;

import me.statuxia.shulkerapi.dto.operation.OperationData;
import me.statuxia.shulkerapi.processor.OperationProcessor;
import me.statuxia.shulkerapi.service.OperationProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OperationProcessorServiceImpl implements OperationProcessorService {

    private final List<OperationProcessor> processors;

    @Autowired
    public OperationProcessorServiceImpl(List<OperationProcessor> processors) {
        this.processors = processors;
    }

    @Override
    @Transactional
    public void process(OperationData data) {
        for (OperationProcessor processor : processors) {
            if (!processor.isSuitable(data)) {
                return;
            }

            processor.validate(data);
            processor.process(data);
        }
    }
}
