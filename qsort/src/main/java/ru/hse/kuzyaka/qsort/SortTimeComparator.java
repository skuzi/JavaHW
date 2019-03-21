package ru.hse.kuzyaka.qsort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;

public class SortTimeComparator {
    public static void main(String[] args) {
        QuickSort.setSortThreshold(30);
        for(int i = 16; i < (1 << 22); i *= 2) {
            var list = new ArrayList<Integer>();
            IntStream.range(0, i).forEach(list::add);
            Collections.shuffle(list);

            long timeCollectionsSort = getTime(() -> Collections.sort(list));
            long timeQuickSort = getTime(() -> QuickSort.sort(list));
            System.out.print("On " + i + " integers ");
            if (timeQuickSort == timeCollectionsSort) {
                System.out.println("draw occurred");
            } else {
                String winner = timeQuickSort > timeCollectionsSort ? "Collections::sort" : "QuickSort::sort";
                System.out.println(winner + " wins by " +
                                Math.abs(timeCollectionsSort - timeQuickSort) + "milliseconds");
            }
        }
    }

    private static long getTime(Runnable function) {
        long timeBegin = System.currentTimeMillis();
        function.run();
        long timeEnd = System.currentTimeMillis();
        return timeEnd - timeBegin;
    }
}
