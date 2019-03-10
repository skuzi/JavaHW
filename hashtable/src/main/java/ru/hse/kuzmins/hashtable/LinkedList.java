package ru.hse.kuzmins.hashtable;

import org.jetbrains.annotations.NotNull;

/** This class implements a linked list. Each <code>Node</code> has access to both previous and next <code>Node</code>. */
public class LinkedList {
    /** First node of the list. */
    private Node begin = null;
    /** Last node of the list. */
    private Node end = null;
    /** Size of the list. */
    private int size = 0;

    /** Constructs an empty list with <code>begin = end = null</code> */
    public LinkedList() {}

    /**
     * Returns the number of stored elements.
     * @return the number of stored elements
     */
    public int size() {
        return size;
    }

    /**
     * Checks if specified object is in the list.
     * @param data object that is looked for
     * @return <code>true</code> if this list contains the object; false otherwise
     */
    public boolean contains(Object data) {
        if (isEmpty()) {
            return false;
        }

        Node node = begin;
        while (node != null && !node.data.equals(data)) {
            node = node.next;
        }

        return node != null;
    }

    /**
     * Returns the element at the specified position in the list.
     * @param index index of the element to return
     * @return the element at the specified position in the list
     */
    public Object get(int index) {
        Node res = getNode(index);
        return res == null ? null : res.data;
    }

    /**
     * Returns the index of the first occurrence of the specified object in the list.
     * @param data object which is looked for
     * @return the index of the first occurrence of the specified object if it occurs in the list; -1 otherwise
     */
    public int indexOf(Object data) {
        if (isEmpty()) {
            return -1;
        }

        Node node = begin;
        int index = 0;
        while (node != null && !node.data.equals(data)) {
            node = node.next;
            index++;
        }

        return node == null ? -1 : index;
    }

    /**
     * Adds the specified object to the end of the list.
     * @param data the object that is added
     */
    public void add(@NotNull Object data) {
        if (isEmpty()) {
            begin = new Node(null, null, data);
            end = begin;
        } else {
            Node node = new Node(end, null, data);
            end.next = node;
            end = node;
        }
        size++;
    }

    /**
     * Removes the element at the specified position in the list.
     * @param index the index of the element that is needed to be removed
     */
    public void remove(int index) {
        Node node = getNode(index);
        if (node == null) {
            return;
        }

        if (node.next != null) {
            node.next.previous = node.previous;
        } else {
            end = node.previous;
        }

        if (node.previous != null) {
            node.previous.next = node.next;
        } else {
            begin = node.next;
        }
        size--;
    }

    /**
     * Assigns the object at position <code>index</code> to value <code>value</code>.
     * @param index the index of the element that has its value changed
     * @param value the new value of the element, mustn't be null
     */
    public void set(int index, @NotNull Object value) {
        Node node = getNode(index);
        if (node != null)
            node.data = value;
    }

    /**
     * Clears the list.
     */
    public void clear() {
        begin = null;
        end = null;
        size = 0;
    }

    /**
     * Checks if the list is empty.
     * @return <code>true</code> if the list is empty; <code>false</code> otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns an array containing all the elements of the list in the proper order (from first to last element).
     * @return an array containing all the elements of the list in the proper order (from first to last element)
     */
    public Object[] toArray() {
        Object[] arrayFromList = new Object[size];

        Node node = begin;
        int index = 0;
        while (node != null) {
            arrayFromList[index++] = node.data;
            node = node.next;
        }

        return arrayFromList;
    }

    /**
     * Returns the node at the specified position in the list.
     * @param index index of the node to return
     * @return the node at the specified position in the list
     */
    private Node getNode(int index) {
        if (index >= size || index < 0) {
            return null;
        }

        Node node = begin;
        while (index != 0) {
            node = node.next;
            index--;
        }

        return node;
    }

    /** Class for storing nodes of the list. */
    private class Node {
        /** Stores the previous node. This field is <code>null</code> if there is no such node. */
        private Node previous;
        /** Stores the next node. This field is <code>null</code> if there is no such node. */
        private Node next;
        /** Stores the data of the node. */
        private @NotNull Object data;

        /**
         * Constructs an instance of <code>Node</code> with specified <code>previous</code>, <code>next</code> and <code>data</code>
         * @param previous the previous node
         * @param next the next node
         * @param data the stored data, mustn't be <code>null</code>
         */
        private Node(Node previous, Node next, @NotNull Object data) {
            this.previous = previous;
            this.next = next;
            this.data = data;
        }
    }
}
