package me.statuxia.shulkerapi.model;

/**
 * Маркерный интерфейс, указывающий что реализация является источником токена
 */
public interface TokenSource {

    Account getAccount();
}
