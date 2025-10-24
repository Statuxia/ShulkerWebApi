package me.statuxia.shulkerapi.service.impl;

import me.statuxia.shulkerapi.dao.impl.FineDAO;
import me.statuxia.shulkerapi.dao.impl.FineLogDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.dto.operation.OperationData;
import me.statuxia.shulkerapi.dto.search.impl.BankCardSearchDTO;
import me.statuxia.shulkerapi.exception.CardException;
import me.statuxia.shulkerapi.exception.FineException;
import me.statuxia.shulkerapi.model.*;
import me.statuxia.shulkerapi.processor.impl.card.PayFineProcessor;
import me.statuxia.shulkerapi.request.CloseFineRequest;
import me.statuxia.shulkerapi.request.CreateFineRequest;
import me.statuxia.shulkerapi.request.EditMessageFineRequest;
import me.statuxia.shulkerapi.request.PayFineRequest;
import me.statuxia.shulkerapi.service.BankCardService;
import me.statuxia.shulkerapi.service.FineService;
import me.statuxia.shulkerapi.service.GameAccountService;
import me.statuxia.shulkerapi.service.OperationProcessorService;
import me.statuxia.shulkerapi.utils.FineDataBuilder;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Transactional
@Service
public class FineServiceImpl implements FineService {

    private final GameAccountService gameAccountService;
    private final FineDAO fineDAO;
    private final FineLogDAO fineLogDAO;
    private final BankCardService bankCardService;
    private final OperationProcessorService operationProcessorService;

    @Autowired
    public FineServiceImpl(
        GameAccountService gameAccountService,
        FineDAO fineDAO,
        FineLogDAO fineLogDAO, BankCardService bankCardService,
        OperationProcessorService operationProcessorService
    ) {
        this.gameAccountService = gameAccountService;
        this.fineDAO = fineDAO;
        this.fineLogDAO = fineLogDAO;
        this.bankCardService = bankCardService;
        this.operationProcessorService = operationProcessorService;
    }

    @Override
    public void pay(TokenData token, PayFineRequest request) {
        final GameAccount gameAccount = gameAccountService.getGameAccount(
            token, request.getGameAccount(),
            TokenAuthorityEnum.PAY_FINE
        );

        final Fine fine = getFine(request.getId());

        if (!Objects.equals(gameAccount, fine.getGameAccount())) {
            throw FineException.UNKNOWN_FINE;
        }

        if (fine.isStatusFinal()) {
            throw FineException.FINE_STATUS_FINALIZED;
        }

        final String number = request.getPaymentCardNumber();
        if (!StringUtils.hasText(number)) {
            throw CardException.UNKNOWN_PAYMENT_CARD;
        }

        final BankCardSearchDTO dto = new BankCardSearchDTO()
            .setCardType(CardType.DIRECT).setNumber(number).setGameAccount(gameAccount);

        final BankCard paymentCard = bankCardService.getPaymentCard(dto, request.getPaymentCardPin());
        final Long value = fine.getFineValue();

        final OperationData data = new OperationData()
            .addProcessor(PayFineProcessor.class)
            .addData(PayFineProcessor.CARD, paymentCard)
            .addData(PayFineProcessor.FINE, fine)
            .addData(PayFineProcessor.VALUE, value);
        operationProcessorService.process(data);
    }

    @Override
    public void create(TokenData token, CreateFineRequest request) {
        final GameAccount gameAccount = gameAccountService.getGameAccount(token,
            request.getGameAccount(),
            TokenAuthorityEnum.CREATE_FINE);
        final GameAccount actionBy = gameAccountService.getActionGameAccount(request.getActionBy());

        if (request.getDueDate().isBeforeNow()) {
            throw FineException.DUE_DATE_IN_PAST;
        }

        final Fine fine = new Fine();
        fine.setGameAccount(gameAccount);
        fine.setActionBy(actionBy.getName());
        fine.setFineValue(request.getValue());
        fine.setDueDate(request.getDueDate());
        fine.setNotified(false);
        fine.setStatus(FineStatus.NEW);
        fine.setStatusDate(DateTime.now());
        fine.setCreateDate(DateTime.now());
        fine.setMessage(request.getMessage());

        fineDAO.save(fine);
    }

    @Override
    public void editMessage(EditMessageFineRequest request) {
        final GameAccount actionBy = gameAccountService.getActionGameAccount(request.getActionBy());

        final Fine fine = getFine(request.getId());
        if (fine.isStatusFinal()) {
            throw FineException.FINE_STATUS_FINALIZED;
        }

        final FineLog log = new FineLog();
        log.setFine(fine);
        log.setAction(FineAction.EDIT_MESSAGE);
        log.setActionBy(actionBy.getName());
        log.setData(new FineDataBuilder().oldNewValue(fine.getMessage(), request.getMessage()).getData());
        fine.setMessage(request.getMessage());

        fineLogDAO.save(log);
        fineDAO.save(fine);
    }

    @Override
    public void close(CloseFineRequest request) {
        final GameAccount actionBy = gameAccountService.getActionGameAccount(request.getActionBy());

        final Fine fine = getFine(request.getId());

        if (fine.isStatusFinal()) {
            throw FineException.FINE_STATUS_FINALIZED;
        }

        final FineLog log = new FineLog();
        log.setFine(fine);
        log.setAction(FineAction.CLOSE);
        log.setActionBy(actionBy.getName());
        log.setData(new FineDataBuilder().oldNewValue(
            MessageService.enumI18n(fine.getStatus()),
            MessageService.enumI18n(FineStatus.CLOSED)
        ).getData());
        log.setLogDate(DateTime.now());
        fine.setStatusDate(DateTime.now());
        fine.setStatus(FineStatus.CLOSED);

        fineLogDAO.save(log);
        fineDAO.save(fine);
    }

    private Fine getFine(Long id) {
        return fineDAO.findById(id).orElseThrow(() -> FineException.UNKNOWN_FINE);
    }
}
