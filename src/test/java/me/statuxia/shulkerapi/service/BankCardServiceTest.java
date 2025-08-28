package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.dao.BankCardDAO;
import me.statuxia.shulkerapi.exception.FundsException;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.service.impl.BankCardServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BankCardServiceTest {

    @InjectMocks
    protected BankCardServiceImpl cardService;

    @Mock
    protected BankCardDAO bankCardDAO;

    @ParameterizedTest
    @MethodSource("withdrawFundsDataSource")
    void withdrawFundsTest(BankCard card, Long amount, Object expectedResult) {
        if (expectedResult instanceof Exception exception) {
            assertThrows(exception.getClass(), () -> cardService.withdrawFunds(card, amount), exception.getMessage());
            return;
        }

        if (expectedResult instanceof BankCard result) {
            cardService.withdrawFunds(card, amount);
            assertEquals(result.getCurrency(), card.getCurrency());
            return;
        }

        fail();
    }

    @ParameterizedTest
    @MethodSource("depositFundsDataSource")
    void depositFundsTest(BankCard card, Long amount, Object expectedResult) {
        if (expectedResult instanceof Exception exception) {
            assertThrows(exception.getClass(), () -> cardService.depositFunds(card, amount), exception.getMessage());
            return;
        }

        if (expectedResult instanceof BankCard result) {
            cardService.depositFunds(card, amount);
            assertEquals(result.getCurrency(), card.getCurrency());
            return;
        }

        fail();
    }

    public static Stream<Arguments> withdrawFundsDataSource() {
        return Stream.of(
            Arguments.of(buildCard(100L), -100L, FundsException.AMOUNT_GREATER_ZERO),
            Arguments.of(buildCard(100L), 0L, FundsException.AMOUNT_GREATER_ZERO),
            Arguments.of(buildCard(100L), 101L, FundsException.NOT_ENOUGH_FUNDS),
            Arguments.of(buildCard(100L), 100L, buildCard(0L))
        );
    }

    public static Stream<Arguments> depositFundsDataSource() {
        return Stream.of(
            Arguments.of(buildCard(100L), -100L, FundsException.AMOUNT_GREATER_ZERO),
            Arguments.of(buildCard(100L), 0L, FundsException.AMOUNT_GREATER_ZERO),
            Arguments.of(buildCard(100L), 100L, buildCard(200L))
        );
    }

    private static BankCard buildCard(Long currency) {
        final BankCard card = new BankCard();
        card.setCurrency(currency);
        return card;
    }
}