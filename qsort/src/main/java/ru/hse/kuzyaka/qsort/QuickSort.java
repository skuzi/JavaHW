package ru.hse.kuzyaka.qsort;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.stream.IntStream;

public class QuickSort {
    private static int sortThreshold = 100;
    private static Random random = new Random();

    public static void setSortThreshold(int sortThreshold) {
        QuickSort.sortThreshold = sortThreshold;
    }

    public static void setRandom(Random random) {
        QuickSort.random = random;
    }

    public static void sort(List<Integer> listToSort) {
        int[] arrayToSort = listToSort.stream().mapToInt(Integer::intValue).toArray();
        var threadPool = new ForkJoinPool();
        threadPool.invoke(new QuickSortTask(arrayToSort));

        listToSort.clear();
        IntStream.range(0, arrayToSort.length).forEach(i -> listToSort.add(arrayToSort[i]));
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