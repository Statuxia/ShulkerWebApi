package me.statuxia.shulkerapi.dao.impl;

import jakarta.transaction.Transactional;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.dto.search.impl.BankCardMemberSearchDTO;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.BankCardMember;
import me.statuxia.shulkerapi.model.CardType;
import me.statuxia.shulkerapi.model.GameAccount;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql("classpath:sql/BankCardMemberDAOTest.sql")
@SpringBootTest
@Transactional
@TestPropertySource("classpath:test-application.properties")
@DirtiesContext
class BankCardMemberDAOTest extends BaseContainerTest {

    @Autowired
    private BankCardMemberDAO bankCardMemberDAO;

    @Test
    void findListByCardTest() {
        final BankCard card = bankCardMemberDAO.getEntityManager().find(BankCard.class, 1L);

        final List<BankCardMember> result = bankCardMemberDAO.findList(
            new BankCardMemberSearchDTO().setCard(card)
        );

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(m -> m.getCard().getId().equals(1L)));
    }

    @Test
    void findListByGameAccountTest() {
        final GameAccount gameAccount = bankCardMemberDAO.getEntityManager().find(GameAccount.class, 1L);

        final List<BankCardMember> result = bankCardMemberDAO.findList(
            new BankCardMemberSearchDTO().setGameAccount(gameAccount)
        );

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(m -> m.getGameAccount().getId().equals(1L)));
    }

    @Test
    void findListByCardAndGameAccountTest() {
        final BankCard card = bankCardMemberDAO.getEntityManager().find(BankCard.class, 1L);
        final GameAccount gameAccount = bankCardMemberDAO.getEntityManager().find(GameAccount.class, 1L);

        final List<BankCardMember> result = bankCardMemberDAO.findList(
            new BankCardMemberSearchDTO().setCard(card).setGameAccount(gameAccount)
        );

        assertEquals(1, result.size());
        assertEquals(1L, result.getFirst().getId());
    }

    @Test
    void findListByAddedAtFromTest() {
        final List<BankCardMember> result = bankCardMemberDAO.findList(
            new BankCardMemberSearchDTO().setAddedAtFrom(new DateTime(2026, 3, 1, 0, 0))
        );

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(m -> m.getAddedAt().isAfter(new DateTime(2026, 3, 1, 0, 0))));
    }

    @Test
    void findListByAddedAtRangeTest() {
        final DateTime from = new DateTime(2026, 1, 1, 0, 0);
        final DateTime to = new DateTime(2026, 2, 1, 0, 0);

        final List<BankCardMember> result = bankCardMemberDAO.findList(
            new BankCardMemberSearchDTO().setAddedAtFrom(from).setAddedAtTo(to)
        );

        assertEquals(2, result.size());
    }

    @Test
    void findListByCreditedFromTest() {
        final List<BankCardMember> result = bankCardMemberDAO.findList(
            new BankCardMemberSearchDTO().setCreditedFrom(100L)
        );

        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(m -> m.getCredited() >= 100L));
    }

    @Test
    void findListByCreditedRangeTest() {
        final List<BankCardMember> result = bankCardMemberDAO.findList(
            new BankCardMemberSearchDTO().setCreditedFrom(100L).setCreditedTo(200L)
        );

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(m -> m.getCredited() >= 100L && m.getCredited() <= 200L));
    }

    @Test
    void findListByDebitedFromTest() {
        final List<BankCardMember> result = bankCardMemberDAO.findList(
            new BankCardMemberSearchDTO().setDebitedFrom(100L)
        );

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(m -> m.getDebited() >= 100L));
    }

    @Test
    void findListByDebitedRangeTest() {
        final List<BankCardMember> result = bankCardMemberDAO.findList(
            new BankCardMemberSearchDTO().setDebitedFrom(50L).setDebitedTo(100L)
        );

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(m -> m.getDebited() >= 50L && m.getDebited() <= 100L));
    }

    @Test
    void countByCardTest() {
        final BankCard card = bankCardMemberDAO.getEntityManager().find(BankCard.class, 1L);

        final long count = bankCardMemberDAO.count(new BankCardMemberSearchDTO().setCard(card));

        assertEquals(2, count);
    }

    @Test
    void saveAndFindByIdTest() {
        final BankCard card = bankCardMemberDAO.getEntityManager().find(BankCard.class, 1L);
        final GameAccount gameAccount = bankCardMemberDAO.getEntityManager().find(GameAccount.class, 3L);

        final BankCardMember member = new BankCardMember();
        member.setCard(card);
        member.setGameAccount(gameAccount);
        member.setPin("9999");
        member.setAddedAt(DateTime.now());
        member.setCredited(0L);
        member.setDebited(0L);

        bankCardMemberDAO.save(member);
        assertNotNull(member.getId());

        assertTrue(bankCardMemberDAO.findById(member.getId()).isPresent());
        assertEquals(CardType.GROUP, bankCardMemberDAO.findById(member.getId()).get().getCard().getType());
    }

    @Test
    void deleteTest() {
        final long countBefore = bankCardMemberDAO.countAll();

        final BankCardMember member = bankCardMemberDAO.findById(1L).orElseThrow();
        bankCardMemberDAO.forceDelete(member);

        assertEquals(countBefore - 1, bankCardMemberDAO.countAll());
        assertTrue(bankCardMemberDAO.findById(1L).isEmpty());
    }
}
