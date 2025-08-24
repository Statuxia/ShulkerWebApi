package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.dao.AccountDAO;
import me.statuxia.shulkerapi.exception.BaseApiException;
import me.statuxia.shulkerapi.model.Account;
import me.statuxia.shulkerapi.service.impl.ValidationServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationServiceTest {

    @InjectMocks
    protected ValidationServiceImpl validationService;

    @Mock
    protected AccountDAO accountDAO;

    @ParameterizedTest
    @MethodSource("validateAccountDataSource")
    void testValidateAccount(Account account, Boolean existsById, Class<BaseApiException> exceptionClass) {
        if (existsById != null) {
            when(accountDAO.existsById(anyLong())).thenReturn(existsById);
        }

        if (exceptionClass == null) {
            assertDoesNotThrow(() -> validationService.validateAccount(account));
        } else {
            assertThrows(exceptionClass, () -> validationService.validateAccount(account));
        }
    }

    public static Stream<Arguments> validateAccountDataSource() {
        return Stream.of(
            Arguments.of(null, null, BaseApiException.class),
            Arguments.of(buildAccount(null), null, BaseApiException.class),
            Arguments.of(buildAccount(1L), false, BaseApiException.class),
            Arguments.of(buildAccount(1L), true, null)
        );
    }

    public static Account buildAccount(Long id) {
        final Account account = new Account();
        account.setId(id);
        return account;
    }
}