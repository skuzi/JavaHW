package ru.hse.kuzyaka.qsort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;

public class SortTimeComparator {
    public static void main(String[] args) {
        for(int i = 2; i < (1 << 22); i *= 2) {
            var list = new ArrayList<Integer>();
            IntStream.range(0, i).forEach(list::add);
            Collections.shuffle(list);

            double timeCollectionsSort = getTime(() -> Collections.sort(list));
            double timeQuickSort = getTime(() -> QuickSort.sort(list));
            String winner = timeQuickSort > timeCollectionsSort ? "Collections::sort" : "QuickSort::sort";
            System.out.println(
                    "On " + i + " integers " + winner + " wins by " +
                            Math.abs(timeCollectionsSort - timeQuickSort) + "milliseconds");
        }
    }

    private static double getTime(Runnable function) {
        double timeBegin = System.currentTimeMillis();
        function.run();
        double timeEnd = System.currentTimeMillis();
        return timeEnd - timeBegin;
    }
}
