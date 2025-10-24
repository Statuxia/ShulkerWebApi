package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.request.CreateFineRequest;
import me.statuxia.shulkerapi.request.EditMessageFineRequest;
import me.statuxia.shulkerapi.request.CloseFineRequest;
import me.statuxia.shulkerapi.request.PayFineRequest;

public interface FineService {

    void pay(TokenData token, PayFineRequest request);

    void create(TokenData token, CreateFineRequest request);

    void editMessage(EditMessageFineRequest request);

    void close(CloseFineRequest request);
}
