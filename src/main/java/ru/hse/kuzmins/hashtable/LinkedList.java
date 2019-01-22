package ru.hse.kuzmins.hashtable;

/**
 * This class implements a linked list. Each <code>Node</code> has access to both previous and next <code>Node</code>.
 */
public class LinkedList {
    /**
     * First node of the list.
     */
    private Node begin = null;
    /**
     * Last node of the list.
     */
    private Node end = null;
    /**
     * Size of the list.
     */
    private int size = 0;

    /**
     * Constructs an empty list with <code>begin = end = null</code>
     */
    LinkedList() {}

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
    public void add(Object data) {
        if (isEmpty()) {
            begin = end = new Node(null, null, data);
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
            node.next.prev = node.prev;
        } else {
            end = node.prev;
        }

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            begin = node.next;
        }
        size--;
    }

    /**
     * Assigns the object at position <code>index</code> to value <code>value</code>.
     * @param index the index of the element that has its value changed
     * @param value the new value of the element
     */
    public void set(int index, Object value) {
        Node node = getNode(index);
        if (node != null)
            node.data = value;
    }

    /**
     * Clears the list.
     */
    public void clear() {
        begin = end = null;
        size = 0;
    }

    /**
     * Checks if the list is empty.
     * @return true if the list is empty; false otherwise
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns an array containing all the elements of the list in the proper order (from first to last element).
     * @return an array containing all the elements of the list in the proper order (from first to last element)
     */
    public Object[] toArray() {
        Object[] arr = new Object[size];

        Node node = begin;
        int index = 0;
        while (node != null) {
            arr[index++] = node.data;
            node = node.next;
        }

        return arr;
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

    /**
     * Class for storing nodes of the list.
     */
    private class Node {
        /**
         * Stores the previous node. This field is <code>null</code> if there is no such node.
         */
        Node prev = null;
        /**
         * Stores the next node. This field is <code>null</code> if there is no such node.
         */
        Node next = null;
        /**
         * Stores the data of the node.
         */
        Object data = null;

        /**
         * Constructs an instance of <code>Node</code> with specified <code>prev</code>,
         *                                                            <code>next</code> and
         *                                                            <code>data</code>
         * @param prev the previous node
         * @param next the next node
         * @param data the stored data
         */
        Node(Node prev, Node next, Object data) {
            this.prev = prev;
            this.next = next;
            this.data = data;
        }
    }
}
