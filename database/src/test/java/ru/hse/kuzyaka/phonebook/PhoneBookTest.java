package ru.hse.kuzyaka.phonebook;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PhoneBookTest {
    private PhoneBook phoneBook = new PhoneBook();

    @BeforeEach
    void setUp() {
        phoneBook.clear();
    }

    @Test
    void getNumberPersonNoEntry() {
        List<PhoneNumber> numbers = phoneBook.getNumbersByPerson("abc");
        assertNull(numbers);
    }

    @Test
    void getNumberPersonEmpty() {
        phoneBook.addRecord("abc", "123");
        phoneBook.addRecord("abc", "125");
        phoneBook.addRecord("abe", "123");

        assertNull(phoneBook.getNumbersByPerson("xxx"));
        assertNull(phoneBook.getPersonsByNumber("111"));
    }

    @Test
    void getNumberPersonHasNumbers() {
        phoneBook.addRecord("abc", "123");
        phoneBook.addRecord("abc", "125");
        phoneBook.addRecord("abe", "123");
        String[] expectedNumbers = {"123", "125"};
        String[] expectedNames = {"abc", "abe"};
        assertArrayEquals(expectedNumbers, phoneBook.getNumbersByPerson("abc").stream().
                map(PhoneNumber::getNumber).toArray());
        assertArrayEquals(expectedNames, phoneBook.getPersonsByNumber("123").stream().
                map(Person::getName).toArray());
    }

    @Test
    void addRecordNotExisting() {
        assertTrue(phoneBook.addRecord("abc", "111"));
        assertTrue(phoneBook.addRecord("xyz", "222"));
    }

    @Test
    void addRecordWithSameName() {
        assertTrue(phoneBook.addRecord("abc", "123"));
        assertTrue(phoneBook.addRecord("abc", "456"));
    }

    @Test
    void addRecordWithSameNumber() {
        assertTrue(phoneBook.addRecord("abc", "123"));
        assertTrue(phoneBook.addRecord("xyz", "123"));
    }

    @Test
    void addRecordSameRecord() {
        assertTrue(phoneBook.addRecord("abc", "123"));
        assertFalse(phoneBook.addRecord("abc", "123"));
    }

    @Test
    void addRecordAfterDelete() {
        assertTrue(phoneBook.addRecord("abc", "123"));
        assertTrue(phoneBook.deleteRecord("abc", "123"));
        assertTrue(phoneBook.addRecord("abc", "123"));
    }

    @Test
    void addRecordEmpty() {
        assertTrue(phoneBook.addRecord("", ""));
        assertTrue(phoneBook.addRecord("", "123"));
        assertTrue(phoneBook.addRecord("abc", ""));
    }

    @Test
    void deleteRecordFromEmpty() {
        assertFalse(phoneBook.deleteRecord("abc", "123"));
        assertFalse(phoneBook.deleteRecord("", "123"));
        assertFalse(phoneBook.deleteRecord("", ""));
    }

    @Test
    void deleteRecordNotExisting() {
        phoneBook.addRecord("abc", "123");
        phoneBook.addRecord("abc", "456");
        assertFalse(phoneBook.deleteRecord("xyz", "123"));
    }

    @Test
    void deleteRecordSimple() {
        phoneBook.addRecord("abc", "123");
        phoneBook.addRecord("xyz", "456");
        assertTrue(phoneBook.deleteRecord("abc", "123"));
        assertTrue(phoneBook.deleteRecord("xyz", "456"));
    }

    @Test
    void deleteRecordDoesNotDeleteAllPairs() {
        phoneBook.addRecord("abc", "123");
        phoneBook.addRecord("xyz", "456");
        phoneBook.addRecord("abc", "456");
        phoneBook.addRecord("xyz", "123");
        assertTrue(phoneBook.deleteRecord("abc", "123"));
        assertTrue(phoneBook.deleteRecord("abc", "456"));
        assertTrue(phoneBook.deleteRecord("xyz", "123"));
        assertTrue(phoneBook.deleteRecord("xyz", "456"));
    }

    @Test
    void changeNameNotExisting() {
        phoneBook.addRecord("abc", "123");
        phoneBook.addRecord("xyz", "456");
        assertFalse(phoneBook.changeName("789", "ooo", "xyz"));
    }

    @Test
    void changeNameSimple() {
        phoneBook.addRecord("abc", "123");
        phoneBook.addRecord("abc", "456");
        phoneBook.addRecord("xyz", "456");
        assertTrue(phoneBook.changeName("123", "abc", "ooo"));
        assertFalse(phoneBook.addRecord("ooo", "123"));
        String[] expected = {"456"};
        assertArrayEquals(expected, phoneBook.getNumbersByPerson("abc").stream().
                map(PhoneNumber::getNumber).toArray());
        assertTrue(phoneBook.addRecord("abc", "123"));
    }

    @Test
    void changeNameDoesNotAddsNewNumbers() {
        phoneBook.addRecord("abc", "123");
        phoneBook.addRecord("abc", "456");
        phoneBook.addRecord("xyz", "456");
        assertFalse(phoneBook.changeName("456", "abc", "xyz"));
        String[] expected = {"456"};
        assertArrayEquals(expected, phoneBook.getNumbersByPerson("xyz").stream().
                map(PhoneNumber::getNumber).toArray());
    }

    @Test
    void changeNumberNotExisting() {
        phoneBook.addRecord("abc", "123");
        phoneBook.addRecord("xyz", "456");
        assertFalse(phoneBook.changeNumber("ooo", "123", "456"));
    }

    @Test
    void changeNumberSimple() {
        phoneBook.addRecord("abc", "123");
        phoneBook.addRecord("abc", "456");
        phoneBook.addRecord("xyz", "456");
        assertTrue(phoneBook.changeNumber("abc", "123", "789"));
        assertFalse(phoneBook.addRecord("abc", "789"));
        assertTrue(phoneBook.addRecord("abc", "123"));
        String[] expected = {"abc"};
        assertArrayEquals(expected, phoneBook.getPersonsByNumber("789").stream().map(Person::getName).toArray());
    }

    @Test
    void changeNumberDoesNotAddsNewNames() {
        phoneBook.addRecord("abc", "123");
        phoneBook.addRecord("abc", "456");
        phoneBook.addRecord("xyz", "456");
        assertTrue(phoneBook.changeNumber("xyz", "456", "23"));
        String[] expected = {"abc"};
        assertArrayEquals(expected, phoneBook.getPersonsByNumber("456").stream().map(Person::getName).toArray());
    }

    @Test
    void getAllRecordsEmpty() {
        assertTrue(phoneBook.getAllRecords().isEmpty());
    }

    @Test
    void getAllRecordsSimple() {
        phoneBook.addRecord("abc", "123");
        phoneBook.addRecord("xyz", "456");
        phoneBook.addRecord("abc", "456");
        String expected = "abc 123" + "abc 456" + "xyz 456";
        assertEquals(expected, phoneBook.getAllRecords().stream().
                map(PhoneBook.Record::toString).
                collect(Collectors.joining()));
    }
}