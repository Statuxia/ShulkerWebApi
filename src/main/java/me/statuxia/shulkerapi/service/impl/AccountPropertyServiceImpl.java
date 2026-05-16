package me.statuxia.shulkerapi.service.impl;

import me.statuxia.shulkerapi.model.Account;
import me.statuxia.shulkerapi.model.AccountProperty;
import me.statuxia.shulkerapi.model.AccountPropertyType;
import me.statuxia.shulkerapi.service.AccountPropertyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы со списком AccountProperty на уровне приложения (без обращений в БД).
 * Принимает уже загруженный список пропертей и выполняет операции над ним.
 */
@Service
public class AccountPropertyServiceImpl implements AccountPropertyService {

    /**
     * Найти первую пропертю по имени.
     *
     * @param properties список пропертей аккаунта
     * @param type       имя пропери в виде константы enum
     * @return Optional с найденной пропертей или пустой
     */
    public Optional<AccountProperty> findByName(List<AccountProperty> properties, AccountPropertyType type) {
        return findByName(properties, type.name());
    }

    /**
     * Найти первую пропертю по имени.
     *
     * @param properties список пропертей аккаунта
     * @param name       имя проперти
     * @return Optional с найденной пропертей или пустой
     */
    public Optional<AccountProperty> findByName(List<AccountProperty> properties, String name) {
        if (properties == null || name == null) {
            return Optional.empty();
        }
        return properties.stream()
            .filter(p -> name.equals(p.getName()))
            .findFirst();
    }

    /**
     * Проверить, существует ли пропертя с данным именем.
     *
     * @param properties список пропертей аккаунта
     * @param type       имя пропери в виде константы enum
     * @return true, если пропертя найдена
     */
    public boolean hasProperty(List<AccountProperty> properties, AccountPropertyType type) {
        return findByName(properties, type).isPresent();
    }

    /**
     * Проверить, существует ли пропертя с данным именем.
     *
     * @param properties список пропертей аккаунта
     * @param name       имя проперти
     * @return true, если пропертя найдена
     */
    public boolean hasProperty(List<AccountProperty> properties, String name) {
        return findByName(properties, name).isPresent();
    }

    /**
     * Получить значение пропертя по имени.
     *
     * @param properties   список пропертей аккаунта
     * @param type         имя пропери в виде константы enum
     * @param defaultValue значение по умолчанию, если пропертя не найдена
     * @return значение пропертя или defaultValue
     */
    public String getValue(List<AccountProperty> properties, AccountPropertyType type, String defaultValue) {
        return getValue(properties, type.name(), defaultValue);
    }

    /**
     * Получить значение пропертя по имени.
     *
     * @param properties   список пропертей аккаунта
     * @param name         имя проперти
     * @param defaultValue значение по умолчанию, если пропертя не найдена
     * @return значение пропертя или defaultValue
     */
    public String getValue(List<AccountProperty> properties, String name, String defaultValue) {
        return findByName(properties, name)
            .map(AccountProperty::getValue)
            .orElse(defaultValue);
    }

    /**
     * Получить значение пропертя по имени без значения по умолчанию.
     *
     * @param properties список пропертей аккаунта
     * @param type       имя пропери в виде константы enum
     * @return Optional со значением или пустой
     */
    public Optional<String> getValue(List<AccountProperty> properties, AccountPropertyType type) {
        return getValue(properties, type.name());
    }

    /**
     * Получить значение пропертя по имени без значения по умолчанию.
     *
     * @param properties список пропертей аккаунта
     * @param name       имя проперти
     * @return Optional со значением или пустой
     */
    public Optional<String> getValue(List<AccountProperty> properties, String name) {
        return findByName(properties, name).map(AccountProperty::getValue);
    }

    /**
     * Создать новую пропертю.
     *
     * @param account аккаунт-владелец
     * @param name    имя проперти
     * @param value   значение
     * @return готовый объект AccountProperty
     */
    public AccountProperty build(Account account, String name, String value) {
        final AccountProperty property = new AccountProperty();
        property.setAccount(account);
        property.setName(name);
        property.setValue(value);
        return property;
    }
}
