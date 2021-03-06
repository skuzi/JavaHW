package ru.hse.kuzyaka.qsort;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.stream.IntStream;

/** Class implementing multithreading quick sort algorithm for sorting integer lists. **/
public class QuickSort {
    private static int sortThreshold = 100;
    private static Random random = new Random(0);

    /**
     * Sets threshold which determines how small the array must be to perform Collections::sort on it (100 by default)
     * @param sortThreshold new threshold
     */
    public static void setSortThreshold(int sortThreshold) {
        QuickSort.sortThreshold = sortThreshold;
    }

    /**
     * Sets random number generator used when pivot element is chosen ({@code java.util.Random(0)} by default)
     * @param random new random number generator
     */
    public static void setRandom(Random random) {
        QuickSort.random = random;
    }

    /**
     * Sorts given list of integers using multithreading
     * @param listToSort list which is to sort
     */
    public static void sort(List<Integer> listToSort) {
        if (isSorted(listToSort)) {
            return;
        }
        int[] arrayToSort = listToSort.stream().mapToInt(Integer::intValue).toArray();
        var threadPool = new ForkJoinPool();
        threadPool.invoke(new QuickSortTask(arrayToSort));

        listToSort.clear();
        IntStream.range(0, arrayToSort.length).forEach(i -> listToSort.add(arrayToSort[i]));
    }

    private static boolean isSorted(List<Integer> list) {
        return IntStream.range(0, list.size() - 1).allMatch(i -> list.get(i) <= list.get(i + 1));
    }

    private static class QuickSortTask extends RecursiveAction {
        private int[] array;
        private int left;
        private int right;

        private QuickSortTask(int[] array, int left, int right) {
            this.array = array;
            this.left = left;
            this.right = right;
        }

        private QuickSortTask(int[] array) {
            this(array, 0, array.length);
        }

        @Override
        protected void compute() {
            if (right - left <= sortThreshold) {
                Arrays.sort(array, left, right);
            } else {
                int middle = partition(array, left, right);
                invokeAll(new QuickSortTask(array, left, middle), new QuickSortTask(array, middle, right));
            }
        }

        private int partition(int[] array, int left, int right) {
            int i = left;
            int j = right - 1;
            int pivot = array[left + random.nextInt(right - left)];
            while (i <= j) {
                while (array[i] < pivot) {
                    i++;
                }
                while (array[j] > pivot) {
                    j--;
                }
                if (i <= j) {
                    int tmp = array[i];
                    array[i] = array[j];
                    array[j] = tmp;
                    i++;
                    j--;
                }
            }
            return j + 1;
        }
    }
}