package ru.hse.kuzyaka.mytreeset;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A {@link MyTreeSet} implementation using balanced binary search tree (i.e. treap).
 * Note that any comparator passed to this tree must be consistent with {@code equals}.
 *
 * @param <E> the type of the elements maintained by this set
 */
public class Treap<E> extends AbstractSet<E> implements MyTreeSet<E> {
    private Node root = null;
    private Comparator<? super E> comparator;
    private long version = 0;
    private Random random = new Random(239);

    /**
     * Constructs a new, empty treap according to the natural ordering of the elements
     */
    @SuppressWarnings("unchecked")
    public Treap() {
        this((Comparator<? super E>) Comparator.naturalOrder());
    }

    /**
     * Constructs a new, empty treap using given comparator.
     *
     * @param comparator comparator used to compare elements
     */
    public Treap(Comparator<? super E> comparator) {
        this.comparator = comparator;
    }

    /**
     * Returns an iterator over the elements in the set.
     *
     * @return an iterator over the elements in the set
     */
    @Override
    @NotNull
    public Iterator<E> iterator() {
        return new TreapIterator();
    }

    /**
     * Returns the number of stored values.
     *
     * @return the number of stored values
     */
    @Override
    public int size() {
        return root != null ? root.size : 0;
    }

    /**
     * Returns an iterator over the elements in descending order. Equivalent to {@code descendingSet().iterator()}
     *
     * @return an iterator over the elements in descending order.
     */
    @Override
    public Iterator<E> descendingIterator() {
        return new DescendingIterator();
    }

    /**
     * Returns a reverse order view of the elements in the set. All changes done to the original set are reflected in this set.
     *
     * @return reverse order view of the elements in the set.
     */
    @Override
    public MyTreeSet<E> descendingSet() {
        return new DescendingTree();
    }

    /**
     * Returns the first(least) element of the set.
     *
     * @return first element of the set
     * @throws NoSuchElementException if the set is empty
     */
    @Override
    public E first() {
        var iterator = iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * Returns the last(greatest) element of the set.
     *
     * @return last element of the set
     * @throws NoSuchElementException if the set is empty
     */
    @Override
    public E last() {
        var descendingIterator = descendingIterator();
        if (descendingIterator.hasNext()) {
            return descendingIterator.next();
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * Return the greatest of all elements in the set strictly less than the given
     *
     * @param e the value to match
     * @return the greatest element strictly less than the given, or {@code null} if there were no such
     * @throws ClassCastException if the specified element can't be compared with the elements in the set
     */
    @Override
    public E lower(@NotNull E e) {
        Node node = root;
        E lastFitting = null;
        while (node != null) {
            if (comparator.compare(node.value, e) >= 0) {
                node = node.left;
            } else {
                lastFitting = node.value;
                node = node.right;
            }
        }
        return lastFitting;
    }

    /**
     * Return the greatest of all elements in the set less than or equal to the given
     *
     * @param e the value to match
     * @return the greatest element less than or equal to the given, or {@code null} if there were no such
     * @throws ClassCastException if the specified element can't be compared with the elements in the set
     */
    @Override
    public E floor(@NotNull E e) {
        if (contains(e)) {
            return e;
        } else {
            return lower(e);
        }
    }

    /**
     * Return the least of all elements in the set greater than or equal to the given
     *
     * @param e the value to match
     * @return the least element greater than or equal to the given, or {@code null} if there were no such
     * @throws ClassCastException if the specified element can't be compared with the elements in the set
     */
    @Override
    public E ceiling(@NotNull E e) {
        if (contains(e)) {
            return e;
        } else {
            return higher(e);
        }
    }

    /**
     * Returns the least of all elements in the set strictly greater than given
     *
     * @param e the value to match
     * @return the least element greater than {@code e}, or {@code null} if there were no such
     * @throws ClassCastException if the specified element can't be compared with the elements in the set
     */
    @Override
    public E higher(@NotNull E e) {
        Node node = root;
        E lastFitting = null;
        while (node != null) {
            if (comparator.compare(node.value, e) <= 0) {
                node = node.right;
            } else {
                lastFitting = node.value;
                node = node.left;
            }
        }
        return lastFitting;
    }

    /**
     * Adds the specified element to the set if it is not already present. If the set contains such element that it is
     * equal to specified, the original element does not change
     *
     * @param e element to add
     * @return {@code true} if the element was actually added; {@code false} otherwise
     * @throws ClassCastException if the object cannot be compared with the elements in this set
     * @throws NullPointerException if {@code o} is {@code null} and this set uses natural ordering, or its comparator does not permit {@code null} elements
     */
    @Override
    public boolean add(E e) {
        if (contains(e)) {
            return false;
        } else {
            var nodePair = split(root, e);

            var newLeft = merge(new NodePair(nodePair.left, new Node(e)));
            var newRight = nodePair.right;

            root = merge(new NodePair(newLeft, newRight));
            version++;
            return true;
        }
    }

    /**
     * Removes object, equal to the specified, from the set.
     *
     * @param o object to remove
     * @return {@code true} if the object was actually removed; {@code false} otherwise
     * @throws ClassCastException if the object cannot be compared with the elements in this set
     * @throws NullPointerException if {@code o} is {@code null} and this set uses natural ordering, or its comparator does not permit {@code null} elements
     */
    @Override
    public boolean remove(Object o) {
        E e = (E) o;
        if (!contains(e)) {
            return false;
        }
        var splitLess = split(root, e);
        E higher = higher(e);
        if (higher == null) {
            root = splitLess.left;
        } else {
            var splitGreater = split(splitLess.right, higher(e));
            root = merge(new NodePair(splitLess.left, splitGreater.right));
        }
        version++;
        return true;
    }

    /**
     * Checks if the set contains the following object.
     *
     * @param o object to look for
     * @return {@code true} or {@code false} whether the set contains this object or not
     * @throws ClassCastException if {@code o} cannot be compared with elements in the set
     * @throws NullPointerException if {@code o} is {@code null} and this set uses natural ordering, or its comparator does not permit {@code null} elements
     */
    @Override
    public boolean contains(Object o) {
        E e = (E) o;
        Node node = root;
        while (node != null) {
            if (comparator.compare(node.value, e) == 0) {
                return true;
            }
            if (comparator.compare(node.value, e) < 0) {
                node = node.right;
            } else {
                node = node.left;
            }
        }
        return false;
    }

    private Node getNext(@NotNull Node node) {
        if (node.right != null) {
            node = node.right;
            while (node.left != null) {
                node = node.left;
            }
            return node;
        } else {
            while (node.direction.equals(Direction.RIGHT)) {
                node = node.ancestor;
            }
            return node.ancestor;
        }
    }

    private Node getPrev(@NotNull Node node) {
        if (node.left != null) {
            node = node.left;
            while (node.right != null) {
                node = node.right;
            }
            return node;
        } else {
            while (node.direction.equals(Direction.LEFT)) {
                node = node.ancestor;
            }
            return node.ancestor;
        }
    }

    private Node merge(@NotNull NodePair nodePair) {
        if (nodePair.left == null) {
            return nodePair.right;
        }
        if (nodePair.right == null) {
            return nodePair.left;
        }

        if (nodePair.left.priority > nodePair.right.priority) {
            nodePair.left.right = merge(new NodePair(nodePair.left.right, nodePair.right));
            nodePair.left.update();
            nodePair.left.right.ancestor = nodePair.left;
            nodePair.left.right.direction = Direction.RIGHT;
            nodePair.left.direction = Direction.ROOT;
            return nodePair.left;
        } else {
            nodePair.right.left = merge(new NodePair(nodePair.left, nodePair.right.left));
            nodePair.right.update();
            nodePair.right.left.ancestor = nodePair.right;
            nodePair.right.left.direction = Direction.LEFT;
            nodePair.right.direction = Direction.ROOT;
            return nodePair.right;
        }
    }

    private NodePair split(Node node, @NotNull E e) {
        if (node == null) {
            return new NodePair(null, null);
        }

        if (comparator.compare(node.value, e) < 0) {
            var split = split(node.right, e);
            node.right = split.left;
            if (node.right != null) {
                node.right.direction = Direction.RIGHT;
                node.right.ancestor = node;
            }
            node.direction = Direction.ROOT;
            node.update();
            return new NodePair(node, split.right);
        } else {
            var split = split(node.left, e);
            node.left = split.right;
            if (node.left != null) {
                node.left.direction = Direction.LEFT;
                node.left.ancestor = node;
            }
            node.direction = Direction.ROOT;
            node.update();
            return new NodePair(split.left, node);
        }
    }

    private int size(Node node) {
        return node == null ? 0 : node.size;
    }

    private enum Direction {
        LEFT, RIGHT, ROOT
    }

    private class TreapIterator implements Iterator<E> {
        private Node pointer;
        private long version;

        private TreapIterator() {
            pointer = root;
            version = Treap.this.version;
            while (pointer != null && pointer.left != null) {
                pointer = pointer.left;
            }
        }

        @Override
        public boolean hasNext() {
            if(this.version != Treap.this.version) {
                throw new ConcurrentModificationException();
            }
            return pointer != null;
        }

        @Override
        public E next() {
            if(this.version != Treap.this.version) {
                throw new ConcurrentModificationException();
            }

            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            E result = pointer.value;
            pointer = getNext(pointer);
            return result;
        }

    }

    private class DescendingIterator extends TreapIterator {
        private Node pointer;
        private long version;

        private DescendingIterator() {
            pointer = root;
            version = Treap.this.version;
            while (pointer != null && pointer.right != null) {
                pointer = pointer.right;
            }
        }

        @Override
        public boolean hasNext() {
            if(this.version != Treap.this.version) {
                throw new ConcurrentModificationException();
            }
            return pointer != null;
        }

        @Override
        public E next() {
            if(this.version != Treap.this.version) {
                throw new ConcurrentModificationException();
            }

            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            E result = pointer.value;
            pointer = getPrev(pointer);
            return result;
        }
    }

    private class Node {
        private Node left;
        private Node right;
        private Node ancestor;
        private E value;
        private int priority;
        private int size;
        private Direction direction;

        private Node(E value) {
            this(value, null, null, Direction.ROOT, null);
        }

        private Node(E value, Node left, Node right, Direction direction, Node ancestor) {
            this.value = value;
            this.left = left;
            this.right = right;
            this.direction = direction;
            this.ancestor = ancestor;
            this.size = size(left) + size(right) + 1;
            priority = random.nextInt();
        }

        private void update() {
            size = size(left) + size(right) + 1;
        }
    }

    private class NodePair {
        private Node left;
        private Node right;

        private NodePair(Node left, Node right) {
            this.left = left;
            this.right = right;
        }
    }

    private class DescendingTree extends AbstractSet<E> implements MyTreeSet<E> {
        @Override
        public Iterator<E> descendingIterator() {
            return Treap.this.iterator();
        }

        @Override
        public MyTreeSet<E> descendingSet() {
            return Treap.this;
        }

        @Override
        public E first() {
            return Treap.this.last();
        }

        @Override
        public E last() {
            return Treap.this.first();
        }

        @Override
        public E lower(E e) {
            return Treap.this.higher(e);
        }

        @Override
        public E floor(E e) {
            return Treap.this.ceiling(e);
        }

        @Override
        public E ceiling(E e) {
            return Treap.this.floor(e);
        }

        @Override
        public E higher(E e) {
            return Treap.this.lower(e);
        }

        @Override
        public Iterator<E> iterator() {
            return Treap.this.descendingIterator();
        }

        @Override
        public int size() {
            return Treap.this.size();
        }

        @Override
        public boolean contains(Object o) {
            return Treap.this.contains(o);
        }

        @Override
        public boolean remove(Object o) {
            return Treap.this.remove(o);
        }

        @Override
        public boolean add(E e) {
            return Treap.this.add(e);
        }
    }
}