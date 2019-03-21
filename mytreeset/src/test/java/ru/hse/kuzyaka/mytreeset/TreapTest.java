package ru.hse.kuzyaka.mytreeset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class TreapTest {

    private Treap<Integer> emptyTreap = new Treap<>();
    private Treap<Integer> numbers;
    private Treap<Integer> treap;

    private Treap<Integer> numbers(int n) {
        Treap<Integer> result = new Treap<>();
        for (int i = 0; i < n; i++) {
            result.add(i);
        }
        return result;
    }

    @BeforeEach
    void setUp() {
        numbers = numbers(10);
        treap = new Treap<>();
    }

    @Test
    void iterator() {
        var iterator = numbers.iterator();
        for (int i = 0; i < 10; i++) {
            assertTrue(iterator.hasNext());
            assertEquals(Integer.valueOf(i), iterator.next());
        }
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void iteratorInvalidatesAfterAdd() {
        var iterator = numbers.iterator();
        for (int i = 0; i < 5; i++) {
            assertTrue(iterator.hasNext());
            assertEquals(Integer.valueOf(i), iterator.next());
        }
        assertTrue(numbers.add(11));
        assertThrows(ConcurrentModificationException.class, iterator::next);
        assertThrows(ConcurrentModificationException.class, iterator::hasNext);
    }

    @Test
    void iteratorDoesNotInvalidatesAfterUnsuccessfulAdd() {
        var iterator = numbers.iterator();
        for (int i = 0; i < 5; i++) {
            assertTrue(iterator.hasNext());
            assertEquals(Integer.valueOf(i), iterator.next());
        }
        assertFalse(numbers.add(3));
        assertFalse(numbers.add(7));
        assertDoesNotThrow(iterator::next);
        assertDoesNotThrow(iterator::hasNext);
    }

    @Test
    void size() {
        assertEquals(0, emptyTreap.size());
        assertEquals(10, numbers.size());
        for (int i = 0; i < 10; i++) {
            assertTrue(numbers.remove(i));
            assertEquals(10 - i - 1, numbers.size());
        }
    }

    @Test
    void descendingIterator() {
        var descendingIterator = numbers.descendingIterator();
        for (int i = 0; i < 10; i++) {
            assertTrue(descendingIterator.hasNext());
            assertEquals(Integer.valueOf(10 - i - 1), descendingIterator.next());
        }
        assertFalse(descendingIterator.hasNext());
        assertThrows(NoSuchElementException.class, descendingIterator::next);
    }

    @Test
    void descendingSetContainsSameValues() {
        var descendingSet = numbers.descendingSet();
        assertEquals(Integer.valueOf(4), descendingSet.higher(5));
        assertEquals(Integer.valueOf(9), descendingSet.first());
        assertArrayEquals(numbers.toArray(), numbers.descendingSet().descendingSet().toArray());
    }

    @Test
    void descendingSetReflectsChanges() {
        var descendingSet = numbers.descendingSet();

        assertTrue(numbers.remove(5));
        assertFalse(descendingSet.contains(5));
        assertTrue(descendingSet.add(100));
        assertTrue(numbers.contains(100));
    }

    @Test
    void first() {
        assertThrows(NoSuchElementException.class, treap::first);
        assertEquals(Integer.valueOf(0), numbers.first());
        assertEquals(Integer.valueOf(9), numbers.descendingSet().first());
        assertTrue(numbers.remove(0));
        assertEquals(Integer.valueOf(1), numbers.first());
    }

    @Test
    void last() {
        assertEquals(Integer.valueOf(9), numbers.last());
        assertEquals(Integer.valueOf(0), numbers.descendingSet().last());
        assertTrue(numbers.remove(9));
        assertEquals(Integer.valueOf(8), numbers.last());
    }

    @Test
    void lower() {
        assertEquals(Integer.valueOf(9), numbers.lower(Integer.MAX_VALUE));
        for (int i = 1; i < 10; i++) {
            assertEquals(Integer.valueOf(i - 1), numbers.lower(i));
        }
        assertNull(emptyTreap.lower(100));
    }

    @Test
    void floor() {
        for (int i = 0; i < 5; i++) {
            treap.add(2 * i);
        }
        for (int i = 0; i < 5; i++) {
            assertEquals(Integer.valueOf(2 * i), treap.floor(2 * i + 1));
        }
        assertNull(treap.ceiling(10));
        assertEquals(Integer.valueOf(8), treap.floor(Integer.MAX_VALUE));
    }

    @Test
    void ceiling() {
        for (int i = 0; i < 5; i++) {
            treap.add(2 * i);
        }
        for (int i = 0; i < 5; i++) {
            assertEquals(Integer.valueOf(2 * i), treap.ceiling(2 * i - 1));
        }
        assertNull(treap.ceiling(9));
    }

    @Test
    void higher() {
        treap.add(1);
        treap.add(-1);
        treap.add(5);
        treap.add(6);
        assertEquals(Integer.valueOf(5), treap.higher(1));
    }

    @Test
    void add() {
        assertTrue(treap.add(1));
        assertTrue(treap.add(-1));
        assertTrue(treap.add(5));
        assertTrue(treap.add(6));
        assertFalse(treap.add(6));
        assertTrue(treap.contains(1));
        assertTrue(treap.contains(5));
    }

    @Test
    void contains() {
        assertFalse(numbers.contains(10));
        assertTrue(numbers.contains(1));
        assertThrows(ClassCastException.class, () -> numbers.contains("abc"));
    }
}