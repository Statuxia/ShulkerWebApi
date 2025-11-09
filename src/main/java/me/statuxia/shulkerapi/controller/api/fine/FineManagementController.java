package me.statuxia.shulkerapi.controller.api.fine;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.statuxia.shulkerapi.annotations.AuthData;
import me.statuxia.shulkerapi.annotations.RequiredAuthority;
import me.statuxia.shulkerapi.controller.api.Controller;
import me.statuxia.shulkerapi.controller.resolver.AuthDataResolver;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.model.TokenAuthorityEnum;
import me.statuxia.shulkerapi.request.CreateFineRequest;
import me.statuxia.shulkerapi.request.EditMessageFineRequest;
import me.statuxia.shulkerapi.request.CloseFineRequest;
import me.statuxia.shulkerapi.request.PayFineRequest;
import me.statuxia.shulkerapi.service.FineService;
import me.statuxia.shulkerapi.swagger.UnknownAccountOperation;
import me.statuxia.shulkerapi.swagger.UnknownActionByAccountOperation;
import me.statuxia.shulkerapi.swagger.controller.FineManagementControllerOperation;
import me.statuxia.shulkerapi.swagger.controller.card.InvalidPaymentPinOperation;
import me.statuxia.shulkerapi.swagger.controller.card.PaymentCardDisabledOperation;
import me.statuxia.shulkerapi.swagger.controller.card.PaymentFromDirectOperation;
import me.statuxia.shulkerapi.swagger.controller.card.UnknownPaymentCardOperation;
import me.statuxia.shulkerapi.swagger.controller.fine.UnknownFineOperation;
import me.statuxia.shulkerapi.swagger.controller.funds.AmountGreaterZeroOperation;
import me.statuxia.shulkerapi.swagger.controller.funds.NotEnoughFundsOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = FineManagementController.PREFIX, headers = AuthDataResolver.X_TOKEN_HEADER)
@Tag(name = "Fine", description = "Контроллер штрафов")
public class FineManagementController implements Controller {

    public static final String PREFIX = "/api/v1/fine";
    public static final String PAY = "/pay";
    public static final String CREATE = "/create";
    public static final String EDIT_MESSAGE = "/edit-message";
    public static final String CLOSE = "/close";

    private final FineService fineService;

    @Autowired
    public FineManagementController(FineService fineService) {
        this.fineService = fineService;
    }

    @PostMapping(value = PAY, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @UnknownPaymentCardOperation
    @PaymentCardDisabledOperation
    @PaymentFromDirectOperation
    @InvalidPaymentPinOperation
    @UnknownFineOperation
    @NotEnoughFundsOperation
    @AmountGreaterZeroOperation
    @FineManagementControllerOperation.Pay
    public ResponseEntity<Void> pay(@AuthData TokenData token, @RequestBody @Valid PayFineRequest request) {
        fineService.pay(token, request);
        return ResponseEntity.ok().build();
    }


    @RequiredAuthority(requireAll = TokenAuthorityEnum.CREATE_FINE)
    @PostMapping(value = CREATE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @UnknownAccountOperation
    @UnknownActionByAccountOperation
    @FineManagementControllerOperation.Create
    public ResponseEntity<Void> create(@AuthData TokenData token, @RequestBody @Valid CreateFineRequest request) {
        fineService.create(token, request);
        return ResponseEntity.ok().build();
    }

    @RequiredAuthority(requireAll = TokenAuthorityEnum.EDIT_MESSAGE_FINE)
    @PostMapping(value = EDIT_MESSAGE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @UnknownFineOperation
    @FineManagementControllerOperation.EditMessage
    public ResponseEntity<Void> editMessage(
        @AuthData TokenData token,
        @RequestBody @Valid EditMessageFineRequest request
    ) {
        fineService.editMessage(request);
        return ResponseEntity.ok().build();
    }

    @RequiredAuthority(requireAll = TokenAuthorityEnum.CLOSE_FINE)
    @PostMapping(value = CLOSE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @UnknownFineOperation
    @FineManagementControllerOperation.Close
    public ResponseEntity<Void> close(
        @AuthData TokenData token,
        @RequestBody @Valid CloseFineRequest request
    ) {
        fineService.close(request);
        return ResponseEntity.ok().build();
    }
}
