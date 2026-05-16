package me.statuxia.shulkerapi.dao.impl;

import jakarta.transaction.Transactional;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.dto.search.impl.BankCardMemberSettingSearchDTO;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.BankCardMember;
import me.statuxia.shulkerapi.model.BankCardMemberSetting;
import me.statuxia.shulkerapi.model.BankCardSettingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Sql("classpath:sql/BankCardMemberSettingDAOTest.sql")
@SpringBootTest
@Transactional
@TestPropertySource("classpath:test-application.properties")
@DirtiesContext
class BankCardMemberSettingDAOTest extends BaseContainerTest {

    @Autowired
    private BankCardMemberSettingDAO bankCardMemberSettingDAO;

    @Test
    void findListByCardTest() {
        final BankCard card = bankCardMemberSettingDAO.getEntityManager().find(BankCard.class, 1L);

        final List<BankCardMemberSetting> result = bankCardMemberSettingDAO.findList(
            new BankCardMemberSettingSearchDTO().setCard(card)
        );

        assertEquals(4, result.size());
        assertTrue(result.stream().allMatch(s -> s.getCard().getId().equals(1L)));
    }

    @Test
    void findListByCardReturnsEmptyForOtherCardTest() {
        final BankCard card = bankCardMemberSettingDAO.getEntityManager().find(BankCard.class, 2L);

        final List<BankCardMemberSetting> result = bankCardMemberSettingDAO.findList(
            new BankCardMemberSettingSearchDTO().setCard(card)
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void findListByBankCardMemberTest() {
        final BankCardMember member = bankCardMemberSettingDAO.getEntityManager().find(BankCardMember.class, 1L);

        final List<BankCardMemberSetting> result = bankCardMemberSettingDAO.findList(
            new BankCardMemberSettingSearchDTO().setBankCardMember(member)
        );

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(s -> s.getBankCardMember().getId().equals(1L)));
    }

    @Test
    void findListByTypeTest() {
        final List<BankCardMemberSetting> result = bankCardMemberSettingDAO.findList(
            new BankCardMemberSettingSearchDTO().setType(BankCardSettingType.DEPOSIT_PERMISSION)
        );

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(s -> s.getType() == BankCardSettingType.DEPOSIT_PERMISSION));
    }

    @Test
    void findListByTypesTest() {
        final List<BankCardMemberSetting> result = bankCardMemberSettingDAO.findList(
            new BankCardMemberSettingSearchDTO().setTypes(
                List.of(BankCardSettingType.DEPOSIT_PERMISSION, BankCardSettingType.WITHDRAW_PERMISSION)
            )
        );

        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(
            s -> s.getType() == BankCardSettingType.DEPOSIT_PERMISSION
                || s.getType() == BankCardSettingType.WITHDRAW_PERMISSION
        ));
    }

    @ParameterizedTest
    @MethodSource("allSettingTypes")
    void findListByEachTypeTest(BankCardSettingType type) {
        final List<BankCardMemberSetting> result = bankCardMemberSettingDAO.findList(
            new BankCardMemberSettingSearchDTO().setType(type)
        );

        assertFalse(result.isEmpty());
        assertTrue(result.stream().allMatch(s -> s.getType() == type));
    }

    @Test
    void countByBankCardMemberTest() {
        final BankCardMember member = bankCardMemberSettingDAO.getEntityManager().find(BankCardMember.class, 1L);

        final long count = bankCardMemberSettingDAO.count(
            new BankCardMemberSettingSearchDTO().setBankCardMember(member)
        );

        assertEquals(2, count);
    }

    @Test
    void saveAndFindByIdTest() {
        final BankCard card = bankCardMemberSettingDAO.getEntityManager().find(BankCard.class, 1L);
        final BankCardMember member = bankCardMemberSettingDAO.getEntityManager().find(BankCardMember.class, 1L);

        final BankCardMemberSetting setting = new BankCardMemberSetting();
        setting.setCard(card);
        setting.setBankCardMember(member);
        setting.setType(BankCardSettingType.TRANSFER_PERMISSION);

        bankCardMemberSettingDAO.save(setting);
        assertNotNull(setting.getId());

        assertTrue(bankCardMemberSettingDAO.findById(setting.getId()).isPresent());
        assertEquals(
            BankCardSettingType.TRANSFER_PERMISSION,
            bankCardMemberSettingDAO.findById(setting.getId()).get().getType()
        );
    }

    @Test
    void deleteTest() {
        final long countBefore = bankCardMemberSettingDAO.countAll();

        final BankCardMemberSetting setting = bankCardMemberSettingDAO.findById(1L).orElseThrow();
        bankCardMemberSettingDAO.forceDelete(setting);

        assertEquals(countBefore - 1, bankCardMemberSettingDAO.countAll());
        assertTrue(bankCardMemberSettingDAO.findById(1L).isEmpty());
    }

    static Stream<BankCardSettingType> allSettingTypes() {
        return Stream.of(
            BankCardSettingType.DEPOSIT_PERMISSION,
            BankCardSettingType.WITHDRAW_PERMISSION,
            BankCardSettingType.TRANSFER_PERMISSION
        );
    }
}
