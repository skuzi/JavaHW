package ru.hse.kuzmins.hashtable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashTableTest {

    private HashTable ht;

    @BeforeEach
    void initTable() {
        ht = new HashTable();
    }


    @Test
    void sizeEmpty() {
        assertEquals(0, ht.size());
    }

    @Test
    void sizeAddSameKeys() {
        ht.put("a", "a");
        assertEquals(1, ht.size());
        ht.put("a", "b");
        assertEquals(1, ht.size());
    }

    @Test
    void sizeAddFew() {
        for (int i = 0; i < 10; i++) {
            ht.put("a" + i, "a");
            assertEquals(i + 1, ht.size());
        }
    }

    @Test
    void sizeAfterRemoval() {
        for (int i = 0; i < 10; i++) {
            ht.put(String.valueOf(i), String.valueOf(i + 1));
        }

        for (int i = 0; i < 5; i++) {
            assertEquals(10 - i, ht.size());
            ht.remove(String.valueOf(2 * i));
        }
        assertEquals(5, ht.size());
    }

    @Test
    void sizeAfterClear() {
        for (int i = 0; i < 200; i++) {
            ht.put(String.valueOf(i), String.valueOf(i + 1));
        }

        ht.clear();
        assertEquals(0, ht.size());
    }

    @Test
    void containsEmpty() {
        assertFalse(ht.contains("abc"));
        assertFalse(ht.contains(""));
        assertFalse(ht.contains("x"));
    }

    @Test
    void containsAfterAdd() {
        ht.put("x", "abc");
        assertFalse(ht.contains("abc"));
        assertTrue(ht.contains("x"));
    }

    @Test
    void containsAfterPuttingSameKeys() {
        ht.put("x", "cde");
        assertTrue(ht.contains("x"));
        assertFalse(ht.contains("cde"));

        ht.put("x", "abc");
        assertFalse(ht.contains("abc"));
        assertTrue(ht.contains("x"));
    }

    @Test
    void containsAfterRemoval() {
        ht.put("abc", "abc");
        ht.put("x", "abc");
        assertTrue(ht.contains("abc"));

        ht.remove("x");
        assertFalse(ht.contains("x"));
        assertTrue(ht.contains("abc"));
    }

    @Test
    void containsAfterClear() {
        ht.put("abc", "abc");
        ht.put("x", "y");
        ht.clear();

        assertFalse(ht.contains("abc"));

        ht.put("x", "y");
        assertTrue(ht.contains("x"));
    }

    @Test
    void getFromEmptyTable() {
        assertNull(ht.get("abc"));
    }

    @Test
    void getExistingKey() {
        ht.put("abc", "xyz");
        assertEquals("xyz", ht.get("abc"));
        ht.put("xyz", "abc");
        assertEquals("abc", ht.get("xyz"));
    }

    @Test
    void getAfterRemoval() {
        ht.put("abc"    , "abc");
        assertEquals("abc", ht.get("abc"));

        ht.remove("abc");
        assertNull(ht.get("abc"));
    }

    @Test
    void getAfterClear() {
        for (int i = 0; i < 10; i++) {
            ht.put(String.valueOf(i), "123");
        }

        ht.clear();
        for (int i = 0; i < 10; i++) {
            assertNull(ht.get(String.valueOf(i)));
        }
    }

    @Test
    void getAfterSeveralPutsToSamePosition() {
        for (int i = 0; i < 10; i++) {
            ht.put("1", String.valueOf(i));
            assertEquals(String.valueOf(i), ht.get("1"));
        }
    }

    @Test
    void putToEmptyPosition() {
        String val = ht.put("abc", "xyz");
        assertNull(val);
    }

    @Test
    void putToOccupiedPosition() {
        String val = ht.put("abc", "xyz");

        val = ht.put("abc", "abc");
        assertEquals("xyz", val);
    }

    @Test
    void putAfterRemoval() {
        ht.put("abc", "xyz");

        ht.remove("abc");
        String val = ht.put("abc", "a");
        assertNull(val);
    }

    @Test
    void removeKeysWithCollision() {
        ht.put("a", "xyz");
        ht.put("k", "abc");

        ht.remove("k");
        assertTrue(ht.contains("a"));
        assertFalse(ht.contains("u"));
        assertFalse(ht.contains("k"));
    }

    @Test
    void removeAfterSeveralPuts() {
        for (int i = 0; i < 20; i++) {
            ht.put("a" + i, "b" + i);
        }

        for (int i = 0; i < 20; i++) {
            assertTrue(ht.contains("a" + i));
        }

        for (int i = 20; i < 30; i++) {
            assertFalse(ht.contains("a" + i));
        }
    }

    @Test
    void clearEmpty() {
        ht.clear();
        assertEquals(0, ht.size());
    }

    @Test
    void clearAfterManyPuts() {
        for (int i = 0; i < 50000; i++) {
            ht.put(String.valueOf(i), String.valueOf(i + 10));
        }
        ht.clear();
        assertEquals(0, ht.size());
    }
}