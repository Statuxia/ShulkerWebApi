package me.statuxia.shulkerapi.controller.api.minigames;

import me.statuxia.shulkerapi.controller.api.Controller;
import me.statuxia.shulkerapi.swagger.AuthOperation;

@AuthOperation
public abstract class MinigamesController implements Controller {

    public static final String PREFIX = "/api/v1/minigames";

    protected MinigamesController() {
    }
}
