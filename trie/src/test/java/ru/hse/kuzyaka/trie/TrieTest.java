package ru.hse.kuzyaka.trie;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TrieTest {
    private Trie trie;

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
        for (int i = 0; i < 100; i++) {
            assertTrue(trie.contains(String.valueOf(i)));
        }
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
        String[] words = {"a", "abacaba", "abcabc", "bc", "abcbac", "adbcc", "abac", "acc"};
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

        assertDoesNotThrow(() -> moveData(trie, getter));

        assertEquals(trie, getter);
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

        assertDoesNotThrow(() -> moveData(trie, getter));

        for (int i = 20; i < 30; i++) {
            assertFalse(getter.contains(String.valueOf(i)));
        }

        assertEquals(trie, getter);
    }

    @Test
    void serializeEmpty() {
        Trie getter = new Trie();
        for (int i = 0; i < 20; i++) {
            getter.add(String.valueOf(i));
        }

        assertDoesNotThrow(() -> moveData(trie, getter));

        for (int i = 0; i < 10; i++) {
            assertFalse(trie.contains(String.valueOf(i)));
        }
        assertEquals(0, getter.size());
    }

    @Test
    void serializeProtocolTest() {
        try (var actual = new ByteArrayOutputStream(); var expected = new ByteArrayOutputStream()) {
            assertDoesNotThrow(() -> simpleTrieToByteArray(expected));

            trie.add("a");
            trie.add("ab");
            trie.add("ac");
            trie.add("b");

            assertDoesNotThrow(() -> trie.serialize(actual));

            assertArrayEquals(expected.toByteArray(), actual.toByteArray());
        } catch (IOException e) {
            System.out.println("Something went wrong with serialization, probable reason is:");
            e.printStackTrace();
        }
    }

    @Test
    void deserializeProtocolTest() {
        Trie expectedTrie;
        try (var expected = new ByteArrayOutputStream()) {
            assertDoesNotThrow(() -> simpleTrieToByteArray(expected));

            expectedTrie = new Trie();
            expectedTrie.add("a");
            expectedTrie.add("ab");
            expectedTrie.add("b");
            expectedTrie.add("ac");

            assertDoesNotThrow(() -> trie.deserialize(new ByteArrayInputStream(expected.toByteArray())));
            assertEquals(expectedTrie, trie);
        } catch (IOException e) {
            System.out.println("Something went wrong with deserialization, probable reason is:");
            e.printStackTrace();
        }

    }

    @Test
    void equalsSimple() {
        var otherTrie = new Trie();
        for(int i = 0; i < 5; i++) {
            trie.add(String.valueOf(i));
            otherTrie.add(String.valueOf(i));
        }
        assertEquals(trie, otherTrie);
    }

    @Test
    void notEquals() {
        var otherTrie = new Trie();
        for(int i = 0; i < 5; i++) {
            trie.add(String.valueOf(i));
            otherTrie.add(String.valueOf(i));
        }
        otherTrie.remove("3");
        assertNotEquals(trie, otherTrie);
    }

    void moveData(Trie from, Trie to) throws IOException {
        ByteArrayInputStream in;
        try (var out = new ByteArrayOutputStream()) {
            assertDoesNotThrow(() -> from.serialize(out));
            in = new ByteArrayInputStream(out.toByteArray());
            assertDoesNotThrow(() -> to.deserialize(in));
        }
    }

    void simpleTrieToByteArray(ByteArrayOutputStream out) throws IOException {
        try (var dataOut = new DataOutputStream(out)) {
            printNode(dataOut, false, 4, 0, '\0', 2);

            dataOut.writeChar('a');
            printNode(dataOut, true, 3, 1, 'a', 2);

            dataOut.writeChar('b');
            printNode(dataOut, true, 1, 2, 'b', 0);

            dataOut.writeChar('c');
            printNode(dataOut, true, 1, 2, 'c', 0);

            dataOut.writeChar('b');
            printNode(dataOut, true, 1, 1, 'b', 0);
        }
    }

    void printNode(DataOutputStream out, boolean isTerminal, int terminalsInSubtree, int depth, char lastOnPath, int nextSize) throws IOException {
        out.writeBoolean(isTerminal);
        out.writeInt(terminalsInSubtree);
        out.writeInt(depth);
        out.writeChar(lastOnPath);
        out.writeInt(nextSize);
    }
}