package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.request.MinigamesJoinServerRequest;
import me.statuxia.shulkerapi.request.MinigamesLeftServerRequest;

public interface MinigamesSessionService {

    void joinServer(MinigamesJoinServerRequest request);

    void leftServer(MinigamesLeftServerRequest request);
}
