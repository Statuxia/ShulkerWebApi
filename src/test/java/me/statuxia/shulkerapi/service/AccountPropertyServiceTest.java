package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.model.Account;
import me.statuxia.shulkerapi.model.AccountProperty;
import me.statuxia.shulkerapi.service.impl.AccountPropertyServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AccountPropertyServiceTest {

    private AccountPropertyService service = new AccountPropertyServiceImpl();

    @Test
    void findByNameFoundTest() {
        final Optional<AccountProperty> result = service.findByName(buildProperties(), "SOME_PROPERTY");

        assertTrue(result.isPresent());
        assertEquals("value-1", result.get().getValue());
    }

    @Test
    void findByNameNotFoundTest() {
        assertTrue(service.findByName(buildProperties(), "UNKNOWN").isEmpty());
    }

    @Test
    void findByNameNullListTest() {
        assertTrue(service.findByName(null, "SOME_PROPERTY").isEmpty());
    }

    @Test
    void findByNameNullNameTest() {
        assertTrue(service.findByName(buildProperties(), (String) null).isEmpty());
    }

    @Test
    void findByNameEmptyListTest() {
        assertTrue(service.findByName(Collections.emptyList(), "SOME_PROPERTY").isEmpty());
    }

    @Test
    void hasPropertyTrueTest() {
        assertTrue(service.hasProperty(buildProperties(), "ANOTHER_PROPERTY"));
    }

    @Test
    void hasPropertyFalseTest() {
        assertFalse(service.hasProperty(buildProperties(), "UNKNOWN"));
    }

    @Test
    void getValuePresentTest() {
        final Optional<String> value = service.getValue(buildProperties(), "ANOTHER_PROPERTY");

        assertTrue(value.isPresent());
        assertEquals("value-2", value.get());
    }

    @Test
    void getValueAbsentTest() {
        assertTrue(service.getValue(buildProperties(), "UNKNOWN").isEmpty());
    }

    @Test
    void getValueDefaultUsedTest() {
        assertEquals("default", service.getValue(buildProperties(), "UNKNOWN", "default"));
    }

    @Test
    void getValueDefaultNotUsedTest() {
        assertEquals("value-1", service.getValue(buildProperties(), "SOME_PROPERTY", "default"));
    }

    @Test
    void buildTest() {
        final Account account = new Account();
        final AccountProperty built = service.build(account, "MY_PROP", "my-value");

        assertNotNull(built);
        assertEquals(account, built.getAccount());
        assertEquals("MY_PROP", built.getName());
        assertEquals("my-value", built.getValue());
    }

    private List<AccountProperty> buildProperties() {
        final Account account = new Account();
        final List<AccountProperty> properties;

        properties = List.of(
            new AccountProperty().setAccount(account).setName("SOME_PROPERTY").setValue("value-1"),
            new AccountProperty().setAccount(account).setName("ANOTHER_PROPERTY").setValue("value-2"),
            new AccountProperty().setAccount(account).setName("SOME_PROPERTY").setValue("value-duplicate")
        );
        return properties;
    }
}