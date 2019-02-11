package ru.hse.kuzmins.hashtable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashTableTest {

    private HashTable table;

    @BeforeEach
    void initTable() {
        table = new HashTable();
    }


    @Test
    void sizeEmpty() {
        assertEquals(0, table.size());
    }

    @Test
    void sizeAddSameKeys() {
        table.put("a", "a");
        assertEquals(1, table.size());
        table.put("a", "b");
        assertEquals(1, table.size());
    }

    @Test
    void sizeAddFew() {
        for (int i = 0; i < 10; i++) {
            table.put("a" + i, "a");
            assertEquals(i + 1, table.size());
        }
    }

    @Test
    void sizeAfterRemoval() {
        for (int i = 0; i < 10; i++) {
            table.put(String.valueOf(i), String.valueOf(i + 1));
        }

        for (int i = 0; i < 5; i++) {
            assertEquals(10 - i, table.size());
            table.remove(String.valueOf(2 * i));
        }
        assertEquals(5, table.size());
    }

    @Test
    void sizeAfterClear() {
        for (int i = 0; i < 200; i++) {
            table.put(String.valueOf(i), String.valueOf(i + 1));
        }

        table.clear();
        assertEquals(0, table.size());
    }

    @Test
    void containsEmpty() {
        assertFalse(table.contains("abc"));
        assertFalse(table.contains(""));
        assertFalse(table.contains("x"));
    }

    @Test
    void containsAfterAdd() {
        table.put("x", "abc");
        assertFalse(table.contains("abc"));
        assertTrue(table.contains("x"));
    }

    @Test
    void containsAfterPuttingSameKeys() {
        table.put("x", "cde");
        assertTrue(table.contains("x"));
        assertFalse(table.contains("cde"));

        table.put("x", "abc");
        assertFalse(table.contains("abc"));
        assertTrue(table.contains("x"));
    }

    @Test
    void containsAfterRemoval() {
        table.put("abc", "abc");
        table.put("x", "abc");
        assertTrue(table.contains("abc"));

        table.remove("x");
        assertFalse(table.contains("x"));
        assertTrue(table.contains("abc"));
    }

    @Test
    void containsAfterClear() {
        table.put("abc", "abc");
        table.put("x", "y");
        table.clear();

        assertFalse(table.contains("abc"));

        table.put("x", "y");
        assertTrue(table.contains("x"));
    }

    @Test
    void getFromEmptyTable() {
        assertNull(table.get("abc"));
    }

    @Test
    void getExistingKey() {
        table.put("abc", "xyz");
        assertEquals("xyz", table.get("abc"));
        table.put("xyz", "abc");
        assertEquals("abc", table.get("xyz"));
    }

    @Test
    void getAfterRemoval() {
        table.put("abc"    , "abc");
        assertEquals("abc", table.get("abc"));

        table.remove("abc");
        assertNull(table.get("abc"));
    }

    @Test
    void getAfterClear() {
        for (int i = 0; i < 10; i++) {
            table.put(String.valueOf(i), "123");
        }

        table.clear();
        for (int i = 0; i < 10; i++) {
            assertNull(table.get(String.valueOf(i)));
        }
    }

    @Test
    void getAfterSeveralPutsToSamePosition() {
        for (int i = 0; i < 10; i++) {
            table.put("1", String.valueOf(i));
            assertEquals(String.valueOf(i), table.get("1"));
        }
    }

    @Test
    void putToEmptyPosition() {
        String value = table.put("abc", "xyz");
        assertNull(value);
    }

    @Test
    void putToOccupiedPosition() {
        String value = table.put("abc", "xyz");

        value = table.put("abc", "abc");
        assertEquals("xyz", value);
    }

    @Test
    void putAfterRemoval() {
        table.put("abc", "xyz");

        table.remove("abc");
        String value = table.put("abc", "a");
        assertNull(value);
    }

    @Test
    void removeKeysWithCollision() {
        table.put("a", "xyz");
        table.put("k", "abc");

        table.remove("k");
        assertTrue(table.contains("a"));
        assertFalse(table.contains("u"));
        assertFalse(table.contains("k"));
    }

    @Test
    void removeAfterSeveralPuts() {
        for (int i = 0; i < 20; i++) {
            table.put("a" + i, "b" + i);
        }

        for (int i = 0; i < 20; i++) {
            assertTrue(table.contains("a" + i));
        }

        for (int i = 20; i < 30; i++) {
            assertFalse(table.contains("a" + i));
        }
    }

    @Test
    void clearEmpty() {
        table.clear();
        assertEquals(0, table.size());
    }

    @Test
    void clearAfterManyPuts() {
        for (int i = 0; i < 50000; i++) {
            table.put(String.valueOf(i), String.valueOf(i + 10));
        }
        table.clear();
        assertEquals(0, table.size());
    }
}