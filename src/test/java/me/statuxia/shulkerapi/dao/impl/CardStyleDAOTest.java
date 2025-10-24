package me.statuxia.shulkerapi.dao.impl;

import liquibase.Liquibase;
import me.statuxia.shulkerapi.configuration.BaseContainerTest;
import me.statuxia.shulkerapi.model.CardStyle;
import me.statuxia.shulkerapi.model.CardStyleType;
import me.statuxia.shulkerapi.model.CardType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@TestPropertySource("classpath:test-application.properties")
@Transactional
@DirtiesContext
class CardStyleDAOTest extends BaseContainerTest {

    @Autowired
    protected CardStyleDAO cardStyleDAO;

    @Test
    void testFindById() {
        final Optional<CardStyle> cardStyle = cardStyleDAO.findById(CardStyleType.DEFAULT);
        assertTrue(cardStyle.isPresent());
        assertEquals(CardStyleType.DEFAULT, cardStyle.get().getType());
        assertEquals(CardType.DIRECT, cardStyle.get().getCardType());
        assertEquals(0L, cardStyle.get().getPrice());
    }
}