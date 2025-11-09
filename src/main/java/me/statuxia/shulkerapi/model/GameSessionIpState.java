package me.statuxia.shulkerapi.model;

/**
 * Состояния сессии для каждой связки {@code GameAccount}-IP<br>
 * <p>
 * Каждое состояние (за исключением {@code OUTDATED}) через какое-то время попадает в упомянутое состояние.
 * <p>
 * По умолчанию переходы в {@code OUTDATED} происходят через:<br>
 * - 5 минут ({@code STARTED}<br>
 * - 7 дней с момента последнего входа ({@code ACCEPTED})<br>
 * - 31 день ({@code REJECTED})<br>
 * - сразу как будет вызыван {@code /verify} и создана новая запись {@code STARTED} ({@code NOT_NOTIFIED})
 * </p>
 */
public enum GameSessionIpState {

    /**
     * Начало сессии.
     * <p>
     * Состояние начала сессии. Используется при создании записи при соблюдении одного из условий:<br>
     * - Нет связки {@code GameAccount} + IP<br>
     * - В найденных сущностях состояние в двух финальных {@code NOT_NOTIFIED}, {@code OUTDATED}
     */
    STARTED,

    /**
     * Бот не смог отправить уведомление пользователю.
     */
    NOT_NOTIFIED,

    /**
     * Пользователь одобрил вход в боте.
     */
    ACCEPTED,

    /**
     * Пользователь отклонил вход в боте.
     */
    REJECTED,

    /**
     * Сессия устарела.
     */
    OUTDATED
}
