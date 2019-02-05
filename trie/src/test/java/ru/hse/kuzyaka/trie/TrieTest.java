package ru.hse.kuzyaka.trie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class TrieTest {

    Trie trie;

    @BeforeEach
    void setUp() {
        trie = new Trie();
    }

    @Test
    void sizeEmpty() {
        assertEquals(0, trie.size());
    }

    @Test
    void sizeAfterAdd() {
        trie.add("aaa");
        trie.add("asd");
        trie.add("a");
        assertEquals(3, trie.size());
    }

    @Test
    void sizeAfterAddSimilar() {
        trie.add("a");
        trie.add("aa");
        trie.add("a");
        trie.add("aa");
        assertEquals(2, trie.size());
    }

    @Test
    void sizeAfterRemoval() {
        trie.add("a");
        trie.add("aa");
        trie.remove("a");
        assertEquals(1, trie.size());
    }

    @Test
    void addSome() {
        for (int i = 0; i < 100; i++) {
            assertTrue(trie.add(String.valueOf(i)));
        }

        assertEquals(100, trie.size());
        assertTrue(trie.contains(String.valueOf(50)));
        assertTrue(trie.contains(String.valueOf(20)));
        assertFalse(trie.contains(String.valueOf(100)));

        for (int i = 10; i < 20; i++) {
            assertFalse(trie.add(String.valueOf(i)));
        }
    }

    @Test
    void addPrefix() {
        assertTrue(trie.add("aaaa"));
        assertTrue(trie.add("aaa"));
        assertTrue(trie.add("aa"));
        assertTrue(trie.add("a"));
    }

    @Test
    void addWithSamePrefix() {
        assertTrue(trie.add("abacaba"));
        assertTrue(trie.add("ababaka"));
    }

    @Test
    void addEmpty() {
        assertTrue(trie.add(""));
    }

    @Test
    void removeSome() {
        assertFalse(trie.remove("aa"));
        for (int i = 0; i < 100; i++) {
            trie.add(String.valueOf(i));
        }

        for (int i = 10; i < 20; i++) {
            assertTrue(trie.remove(String.valueOf(i)));
        }

        for (int i = 10; i < 20; i++) {
            assertFalse(trie.remove(String.valueOf(i)));
        }

        assertEquals(90, trie.size());
    }

    @Test
    void removeEmpty() {
        assertFalse(trie.remove(""));
    }

    @Test
    void removeNotAdded() {
        for (int i = 0; i < 100; i++) {
            assertFalse(trie.remove(String.valueOf(i)));
        }
    }

    @Test
    void containsEmpty() {
        assertFalse(trie.contains(""));
        trie.add("");
        assertTrue(trie.contains(""));
    }

    @Test
    void containsPrefix() {
        trie.add("aaaa");
        assertTrue(trie.contains("aaaa"));
        assertFalse(trie.contains("aaa"));
        assertFalse(trie.contains("aa"));
        assertFalse(trie.contains("a"));
    }

    @Test
    void containsRemoved() {
        trie.add("aaaa");
        trie.add("aaa");
        trie.add("aa");
        trie.add("a");
        assertTrue(trie.contains("aaa"));
        trie.remove("aaa");
        assertFalse(trie.contains("aaa"));
    }

    @Test
    void containsNotAdded() {
        for (int i = 0; i < 100; i++) {
            assertFalse(trie.contains(String.valueOf(i)));
        }
    }

    @Test
    void howManyStartsWithPrefix() {
        String words[] = {"a", "abacaba", "abcabc", "bc", "abcbac", "adbcc", "abac", "acc"};
        for (String word : words) {
            trie.add(word);
        }
        assertTrue(trie.contains("abacaba"));
        assertEquals(7, trie.howManyStartsWithPrefix("a"));
        assertEquals(0, trie.howManyStartsWithPrefix("c"));
        assertEquals(0, trie.howManyStartsWithPrefix("abcbc"));
        assertEquals(2, trie.howManyStartsWithPrefix("aba"));
        assertEquals(1, trie.howManyStartsWithPrefix("ad"));
        assertEquals(1, trie.howManyStartsWithPrefix("b"));
        assertEquals(1, trie.howManyStartsWithPrefix("abacaba"));
        assertEquals(0, trie.howManyStartsWithPrefix("abacabaa"));
        assertEquals(4, trie.howManyStartsWithPrefix("ab"));
        assertEquals(2, trie.howManyStartsWithPrefix("aba"));
    }

    @Test
    void serializeMoveData() {
        for (int i = 0; i < 10; i++) {
            trie.add(String.valueOf(i));
        }
        Trie getter = new Trie();

        moveData(trie, getter);

        for (int i = 0; i < 10; i++) {
            assertTrue(getter.contains(String.valueOf(i)));
        }

        assertEquals(trie.size(), getter.size());
    }

    @Test
    void serializeReplaceData() {
        for (int i = 0; i < 10; i++) {
            trie.add(String.valueOf(i));
        }

        Trie getter = new Trie();
        for (int i = 20; i < 30; i++) {
            getter.add(String.valueOf(i));
        }

        moveData(trie, getter);

        for (int i = 20; i < 30; i++) {
            assertFalse(getter.contains(String.valueOf(i)));
        }

        assertEquals(trie.size(), getter.size());
    }

    @Test
    void serializeEmpty() {

        Trie getter = new Trie();
        for (int i = 0; i < 20; i++) {
            getter.add(String.valueOf(i));
        }

        moveData(trie, getter);

        for (int i = 0; i < 10; i++) {
            assertFalse(trie.contains(String.valueOf(i)));
        }
        assertEquals(0, getter.size());
    }

    void moveData(Trie from, Trie to) {
        var out = new ByteArrayOutputStream();
        assertDoesNotThrow(() -> from.serialize(out));
        var in = new ByteArrayInputStream(out.toByteArray());
        assertDoesNotThrow(() -> to.deserialize(in));
    }
}