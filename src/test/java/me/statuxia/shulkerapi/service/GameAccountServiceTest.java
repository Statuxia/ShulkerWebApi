package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.dao.GameAccountDAO;
import me.statuxia.shulkerapi.dto.TokenData;
import me.statuxia.shulkerapi.exception.AccountException;
import me.statuxia.shulkerapi.model.Account;
import me.statuxia.shulkerapi.model.GameAccount;
import me.statuxia.shulkerapi.model.SessionToken;
import me.statuxia.shulkerapi.service.impl.GameAccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Lazy;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameAccountServiceTest {

    @InjectMocks
    protected GameAccountServiceImpl gameAccountService;

    @Mock
    protected GameAccountDAO gameAccountDAO;

    @Mock
    @Lazy
    protected GameAccountServiceImpl gameAccountServiceMock;

    @BeforeEach
    void setUp() {
        when(gameAccountServiceMock.validateOwnedWithResult(any(TokenData.class), any(GameAccount.class)))
            .then(k -> gameAccountService.validateOwnedWithResult(k.getArgument(0), k.getArgument(1)));
    }

    @ParameterizedTest
    @MethodSource("validateOwnedDataSource")
    void validateOwnedTest(GameAccount target, List<GameAccount> currentAccounts, Exception exception) {
        when(gameAccountDAO.findByDiscordAccount(null)).thenReturn(currentAccounts);

        final SessionToken source = new SessionToken();
        source.setAccount(new Account());
        final TokenData token = new TokenData("", source);
        if (exception != null) {
            assertThrows(exception.getClass(), () -> gameAccountService.validateOwned(token, target));
            return;
        }

        assertDoesNotThrow(() -> gameAccountService.validateOwned(token, target));
    }

    public static Stream<Arguments> validateOwnedDataSource() {
        return Stream.of(
            Arguments.of(buildGameAccount(1L), List.of(), AccountException.UNKNOWN_ACCOUNT),
            Arguments.of(buildGameAccount(1L), List.of(buildGameAccount(2L)), AccountException.UNKNOWN_ACCOUNT),
            Arguments.of(buildGameAccount(1L), List.of(buildGameAccount(1L)), null)
        );
    }

    public static GameAccount buildGameAccount(Long id) {
        final GameAccount gameAccount = new GameAccount();
        gameAccount.setId(id);
        return gameAccount;
    }
}