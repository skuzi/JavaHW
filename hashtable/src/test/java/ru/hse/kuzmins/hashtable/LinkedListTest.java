package ru.hse.kuzmins.hashtable;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTest {

    private LinkedList lst;

    @BeforeEach
    void initList() {
        lst = new LinkedList();
    }

    @Test
    void sizeEmptyLit() {
        assertEquals(0, lst.size());
    }

    @Test
    void sizeAfterAdds() {
        for (int i = 0; i < 100; i++) {
            lst.add(i);
        }

        assertEquals(100, lst.size());
    }

    @Test
    void sizeAfterAddsAndRemovals() {
        for (int i = 0; i < 100; i++) {
            lst.add(String.valueOf(i));
        }

        for (int i = 0; i < 20; i++) {
            lst.remove(lst.size() - i - 1);
        }

        for (int i = 0; i < 20; i++) {
            lst.remove(0);
        }

        assertEquals(60, lst.size());
    }

    @Test
    void sizeAfterClear() {
        for (int i = 0; i < 100; i++) {
            lst.add(String.valueOf(i));
        }

        lst.clear();
        assertEquals(0, lst.size());
    }

    @Test
    void containsEmpty() {
        assertFalse(lst.contains(5));
        assertFalse(lst.contains("abc"));
        assertFalse(lst.contains(""));
    }

    @Test
    void containsExistingElements() {
        lst.add(5);
        lst.add(6);
        assertTrue(lst.contains(5));
        assertTrue(lst.contains(6));
    }

    @Test
    void containsMissingElements() {
        lst.add(5);
        lst.add(6);
        assertFalse(lst.contains(7));
        assertFalse(lst.contains("abc"));
    }

    @Test
    void getNegativeIndex() {
        lst.add(1);
        lst.add(2);
        assertNull(lst.get(-1));
    }

    @Test
    void getExistingIndex() {
        lst.add(1);
        lst.add("abc");
        assertEquals(1, lst.get(0));
        assertEquals("abc", lst.get(1));
    }

    @Test
    void getIndexOutOfBounds() {
        for (int i = 0; i < 10; i++) {
            lst.add(i);
        }
        assertNull(lst.get(10));
    }

    @Test
    void indexOfEmptyList() {
        assertEquals(-1, lst.indexOf("abc"));
        assertEquals(-1, lst.indexOf(""));
    }

    @Test
    void indexOfExistingElement() {
        lst.add(1);
        lst.add("1");
        assertEquals(0, lst.indexOf(1));
        assertEquals(1, lst.indexOf("1"));
    }

    @Test
    void indexOfMultipleOccurrences() {
        for (int i = 0; i < 5; i++) {
            lst.add(1);
            lst.add("1");
        }
        assertEquals(0, lst.indexOf(1));
        assertEquals(1, lst.indexOf("1"));
    }

    @Test
    void addOneElement() {
        lst.add(1);
        assertEquals(1, lst.size());
        assertEquals(0, lst.indexOf(1));
        assertTrue(lst.contains(1));
    }

    @Test
    void addManyElements() {
        for (int i = 0; i < 10000; i++) {
            lst.add(i);
            lst.add(String.valueOf(i));
        }

        for (int i = 0; i < 10000; i++) {
            assertTrue(lst.contains(i));
            assertTrue(lst.contains(String.valueOf(i)));
        }

        assertEquals(20000, lst.size());
    }

    @Test
    void removeNegativeIndex() {
        for (int i = 0; i < 10; i++) {
            lst.add(String.valueOf(i));
        }

        lst.remove(-1);
        assertEquals(10, lst.size());
    }

    @Test
    void removeIndexOutOfBounds() {
        for (int i = 0; i < 10; i++) {
            lst.add(String.valueOf(i));
        }

        lst.remove(10);
        assertEquals(10, lst.size());
    }

    @Test
    void removeExistingElement() {
        for (int i = 0; i < 10; i++) {
            lst.add(String.valueOf(i));
        }

        lst.remove(0);
        lst.remove(5);

        assertEquals(8, lst.size());
        assertFalse(lst.contains("0"));
        assertTrue(lst.contains("5"));
        assertFalse(lst.contains("6"));
    }

    @Test
    void setIndexOutOfBounds() {
        for (int i = 0; i < 10; i++) {
            lst.add(String.valueOf(i));
        }

        lst.set(-1, "abc");
        lst.set(10, "abc");

        for (int i = 0; i < 10; i++) {
            assertEquals(String.valueOf(i), lst.get(i));
        }
    }

    @Test
    void setExistingIndex() {
        for (int i = 0; i < 100; i++) {
            lst.add(String.valueOf(i));
        }

        lst.set(5, "100");
        lst.set(10, "abc");

        assertEquals(5, lst.indexOf("100"));
        assertEquals(10, lst.indexOf("abc"));
        assertEquals(100, lst.size());
        assertFalse(lst.contains("5"));
        assertFalse(lst.contains("10"));
    }

    @Test
    void clearFewElements() {
        for (int i = 0; i < 200; i++) {
            lst.add(String.valueOf(i));
        }

        lst.clear();
        assertEquals(0, lst.size());
    }

    @Test
        //for time purposes only
    void clearManyElements() {
        for (int i = 0; i < 10000000; i++) {
            lst.add(String.valueOf(i));
        }

        lst.clear();
        assertEquals(0, lst.size());
        assertFalse(lst.contains("10000"));
        assertEquals(-1, lst.indexOf("5"));
    }

    @Test
    void emptyEmptyList() {
        assertTrue(lst.isEmpty());
    }

    @Test
    void emptyNonEmptyList() {
        for (int i = 0; i < 5; i++) {
            lst.add(lst);
        }

        assertFalse(lst.isEmpty());
    }

    @Test
    void emptyAfterClear() {
        assertTrue(lst.isEmpty());

        for (int i = 0; i < 5; i++) {
            lst.add(String.valueOf(i));
        }

        assertFalse(lst.isEmpty());

        lst.clear();
        assertTrue(lst.isEmpty());
        assertEquals(0, lst.size());
    }

    @Test
    void toArrayEmptyList() {
        Object[] a = new Object[0];
        assertArrayEquals(a, lst.toArray());
    }

    @Test
    void toArrayNonEmptyList() {
        Object[] a = new Object[5];
        for(int i = 0; i < 5; i++) {
            a[i] = String.valueOf(i);
            lst.add(String.valueOf(i));
        }

        assertArrayEquals(a, lst.toArray());
    }

    @Test
    void toArrayAfterRemoval() {
        for(int i = 0; i < 5; i++) {
            lst.add(String.valueOf(i)); 
        }
        lst.remove(3);
        lst.remove(1);

        Object[] a = new Object[3];
        for(int i = 0; i < 3; i++) {
            a[i] = String.valueOf(i * 2);
        }

        assertArrayEquals(a, lst.toArray());
    }
}