package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.model.Account;
import me.statuxia.shulkerapi.model.AccountProperty;
import me.statuxia.shulkerapi.model.AccountPropertyType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы со списком AccountProperty на уровне приложения (без обращений в БД).
 * Принимает уже загруженный список пропертей и выполняет операции над ним.
 */
@Service
public interface AccountPropertyService {

    /**
     * Найти первую пропертю по имени.
     *
     * @param properties список пропертей аккаунта
     * @param type       имя пропери в виде константы enum
     * @return Optional с найденной пропертей или пустой
     */
    Optional<AccountProperty> findByName(List<AccountProperty> properties, AccountPropertyType type);

    /**
     * Найти первую пропертю по имени.
     *
     * @param properties список пропертей аккаунта
     * @param name       имя проперти
     * @return Optional с найденной пропертей или пустой
     */
    Optional<AccountProperty> findByName(List<AccountProperty> properties, String name);

    /**
     * Проверить, существует ли пропертя с данным именем.
     *
     * @param properties список пропертей аккаунта
     * @param type       имя пропери в виде константы enum
     * @return true, если пропертя найдена
     */
    boolean hasProperty(List<AccountProperty> properties, AccountPropertyType type);

    /**
     * Проверить, существует ли пропертя с данным именем.
     *
     * @param properties список пропертей аккаунта
     * @param name       имя проперти
     * @return true, если пропертя найдена
     */
    boolean hasProperty(List<AccountProperty> properties, String name);

    /**
     * Получить значение пропертя по имени.
     *
     * @param properties   список пропертей аккаунта
     * @param type         имя пропери в виде константы enum
     * @param defaultValue значение по умолчанию, если пропертя не найдена
     * @return значение пропертя или defaultValue
     */
    String getValue(List<AccountProperty> properties, AccountPropertyType type, String defaultValue);

    /**
     * Получить значение пропертя по имени.
     *
     * @param properties   список пропертей аккаунта
     * @param name         имя проперти
     * @param defaultValue значение по умолчанию, если пропертя не найдена
     * @return значение пропертя или defaultValue
     */
    String getValue(List<AccountProperty> properties, String name, String defaultValue);

    /**
     * Получить значение пропертя по имени без значения по умолчанию.
     *
     * @param properties список пропертей аккаунта
     * @param type       имя пропери в виде константы enum
     * @return Optional со значением или пустой
     */
    Optional<String> getValue(List<AccountProperty> properties, AccountPropertyType type);

    /**
     * Получить значение пропертя по имени без значения по умолчанию.
     *
     * @param properties список пропертей аккаунта
     * @param name       имя проперти
     * @return Optional со значением или пустой
     */
    Optional<String> getValue(List<AccountProperty> properties, String name);

    /**
     * Создать новую пропертю.
     *
     * @param account аккаунт-владелец
     * @param name    имя проперти
     * @param value   значение
     * @return готовый объект AccountProperty
     */
    AccountProperty build(Account account, String name, String value);
}
