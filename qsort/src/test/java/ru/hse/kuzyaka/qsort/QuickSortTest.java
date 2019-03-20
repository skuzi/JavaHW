package ru.hse.kuzyaka.qsort;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class QuickSortTest {
    private List<Integer> list;
    private Random random = new Random(1);

    @BeforeAll
    static void setUp() {
        QuickSort.setRandom(new Random(0));
        QuickSort.setSortThreshold(50);
    }

    @AfterEach
    void shutDown() {
        list.clear();
    }

    @Test
    void testSmall1() {
        list = newList(10);
        QuickSort.sort(list);
        assertTrue(isSorted(list));
    }

    @Test
    void testSmall2() {
        list = newList(1000);
        QuickSort.sort(list);
        assertTrue(isSorted(list));
    }

    @Test
    void testBig1() {
        list = newList(100_000);
        QuickSort.sort(list);
        assertTrue(isSorted(list));
    }

    @Test
    void testBig2(){
        list = newList(1_000_000);
        QuickSort.sort(list);
        assertTrue(isSorted(list));
    }

    private List<Integer> newList(int n) {
        var list = new ArrayList<Integer>();
        IntStream.range(0, n).forEach(i -> list.add(random.nextInt()));
        return list;
    }

    private boolean isSorted(List<Integer> list) {
        return IntStream.range(0, list.size() - 1).allMatch(i -> list.get(i) <= list.get(i + 1));
    }

}