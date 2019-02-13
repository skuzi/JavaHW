package ru.hse.kuzmins.hashtable;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTest {

    private LinkedList list;

    @BeforeEach
    void initList() {
        list = new LinkedList();
    }

    @Test
    void sizeEmptyLit() {
        assertEquals(0, list.size());
    }

    @Test
    void sizeAfterAdds() {
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }

        assertEquals(100, list.size());
    }

    @Test
    void sizeAfterAddsAndRemovals() {
        for (int i = 0; i < 100; i++) {
            list.add(String.valueOf(i));
        }

        for (int i = 0; i < 20; i++) {
            list.remove(list.size() - i - 1);
        }

        for (int i = 0; i < 20; i++) {
            list.remove(0);
        }

        assertEquals(60, list.size());
    }

    @Test
    void sizeAfterClear() {
        for (int i = 0; i < 100; i++) {
            list.add(String.valueOf(i));
        }

        list.clear();
        assertEquals(0, list.size());
    }

    @Test
    void containsEmpty() {
        assertFalse(list.contains(5));
        assertFalse(list.contains("abc"));
        assertFalse(list.contains(""));
    }

    @Test
    void containsExistingElements() {
        list.add(5);
        list.add(6);
        assertTrue(list.contains(5));
        assertTrue(list.contains(6));
    }

    @Test
    void containsMissingElements() {
        list.add(5);
        list.add(6);
        assertFalse(list.contains(7));
        assertFalse(list.contains("abc"));
    }

    @Test
    void getNegativeIndex() {
        list.add(1);
        list.add(2);
        assertNull(list.get(-1));
    }

    @Test
    void getExistingIndex() {
        list.add(1);
        list.add("abc");
        assertEquals(1, list.get(0));
        assertEquals("abc", list.get(1));
    }

    @Test
    void getIndexOutOfBounds() {
        for (int i = 0; i < 10; i++) {
            list.add(i);
        }
        assertNull(list.get(10));
    }

    @Test
    void indexOfEmptyList() {
        assertEquals(-1, list.indexOf("abc"));
        assertEquals(-1, list.indexOf(""));
    }

    @Test
    void indexOfExistingElement() {
        list.add(1);
        list.add("1");
        assertEquals(0, list.indexOf(1));
        assertEquals(1, list.indexOf("1"));
    }

    @Test
    void indexOfMultipleOccurrences() {
        for (int i = 0; i < 5; i++) {
            list.add(1);
            list.add("1");
        }
        assertEquals(0, list.indexOf(1));
        assertEquals(1, list.indexOf("1"));
    }

    @Test
    void addOneElement() {
        list.add(1);
        assertEquals(1, list.size());
        assertEquals(0, list.indexOf(1));
        assertTrue(list.contains(1));
    }

    @Test
    void addManyElements() {
        for (int i = 0; i < 10000; i++) {
            list.add(i);
            list.add(String.valueOf(i));
        }

        for (int i = 0; i < 10000; i++) {
            assertTrue(list.contains(i));
            assertTrue(list.contains(String.valueOf(i)));
        }

        assertEquals(20000, list.size());
    }

    @Test
    void removeNegativeIndex() {
        for (int i = 0; i < 10; i++) {
            list.add(String.valueOf(i));
        }

        list.remove(-1);
        assertEquals(10, list.size());
    }

    @Test
    void removeIndexOutOfBounds() {
        for (int i = 0; i < 10; i++) {
            list.add(String.valueOf(i));
        }

        list.remove(10);
        assertEquals(10, list.size());
    }

    @Test
    void removeExistingElement() {
        for (int i = 0; i < 10; i++) {
            list.add(String.valueOf(i));
        }

        list.remove(0);
        list.remove(5);

        assertEquals(8, list.size());
        assertFalse(list.contains("0"));
        assertTrue(list.contains("5"));
        assertFalse(list.contains("6"));
    }

    @Test
    void setIndexOutOfBounds() {
        for (int i = 0; i < 10; i++) {
            list.add(String.valueOf(i));
        }

        list.set(-1, "abc");
        list.set(10, "abc");

        for (int i = 0; i < 10; i++) {
            assertEquals(String.valueOf(i), list.get(i));
        }
    }

    @Test
    void setExistingIndex() {
        for (int i = 0; i < 100; i++) {
            list.add(String.valueOf(i));
        }

        list.set(5, "100");
        list.set(10, "abc");

        assertEquals(5, list.indexOf("100"));
        assertEquals(10, list.indexOf("abc"));
        assertEquals(100, list.size());
        assertFalse(list.contains("5"));
        assertFalse(list.contains("10"));
    }

    @Test
    void clearFewElements() {
        for (int i = 0; i < 200; i++) {
            list.add(String.valueOf(i));
        }

        list.clear();
        assertEquals(0, list.size());
    }

    @Test
        //for time purposes only
    void clearManyElements() {
        for (int i = 0; i < 10000000; i++) {
            list.add(String.valueOf(i));
        }

        list.clear();
        assertEquals(0, list.size());
        assertFalse(list.contains("10000"));
        assertEquals(-1, list.indexOf("5"));
    }

    @Test
    void emptyEmptyList() {
        assertTrue(list.isEmpty());
    }

    @Test
    void emptyNonEmptyList() {
        for (int i = 0; i < 5; i++) {
            list.add(list);
        }

        assertFalse(list.isEmpty());
    }

    @Test
    void emptyAfterClear() {
        assertTrue(list.isEmpty());

        for (int i = 0; i < 5; i++) {
            list.add(String.valueOf(i));
        }

        assertFalse(list.isEmpty());

        list.clear();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void toArrayEmptyList() {
        Object[] emptyArray = new Object[0];
        assertArrayEquals(emptyArray, list.toArray());
    }

    @Test
    void toArrayNonEmptyList() {
        Object[] array = new Object[5];
        for(int i = 0; i < 5; i++) {
            array[i] = String.valueOf(i);
            list.add(String.valueOf(i));
        }

        assertArrayEquals(array, list.toArray());
    }

    @Test
    void toArrayAfterRemoval() {
        for(int i = 0; i < 5; i++) {
            list.add(String.valueOf(i));
        }
        list.remove(3);
        list.remove(1);

        Object[] array = new Object[3];
        for(int i = 0; i < 3; i++) {
            array[i] = String.valueOf(i * 2);
        }

        assertArrayEquals(array, list.toArray());
    }
}