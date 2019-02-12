package ru.hse.kuzyaka;

import org.jetbrains.annotations.NotNull;
import java.util.*;

public class Treap<E> extends AbstractSet<E> implements MyTreeSet<E> {
    private Node root;
    private Treap<E> descendingSet;
    private Comparator<? super E> comparator;
    private Random random;
    private boolean isDescending;

    @SuppressWarnings("unchecked")
    public Treap() {
        this((Comparator<? super E>) Comparator.naturalOrder());
    }

    public Treap(Comparator<? super E> comparator) {
        root = new Node();
        this.comparator = comparator;
        random = new Random(239);
        isDescending = false;
        descendingSet = new Treap<>(comparator.reversed(), root, random, this, true);
    }

    private Treap(Comparator<? super E> comparator, Node root, Random random, Treap<E> twinSet, boolean isDescending) {
        this.comparator = comparator;
        this.root = root;
        this.random = new Random(239);
        this.random = random;
        this.descendingSet = twinSet;
        this.isDescending = isDescending;
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return new TreapIterator();
    }

    @Override
    public int size() {
        return root != null ? root.size : 0;
    }

    @Override
    public Iterator<E> descendingIterator() {
        return descendingSet.iterator();
    }

    @Override
    public MyTreeSet<E> descendingSet() {
        return descendingSet;
    }

    @Override
    public E first() {
        var iterator = iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            throw new NoSuchElementException();
        }
    }

    @Override
    public E last() {
        return descendingSet.first();
    }

    @Override
    public E lower(@NotNull E e) {
        if (root == null) {
            return null;
        }
        return root.lower(e);
    }

    @Override
    public E floor(@NotNull E e) {
        if (root == null) {
            return null;
        } else if (root.contains(e)) {
            return e;
        } else {
            return root.lower(e);
        }
    }

    @Override
    public E ceiling(@NotNull E e) {
        return descendingSet.floor(e);
    }

    @Override
    public E higher(@NotNull E e) {
        return descendingSet.lower(e);
    }

    @Override
    public boolean add(E e) {
        if(root == null) {
            root = new Node(e);
            root.update();
            return true;
        } else if(root.contains(e)){
            return false;
        } else {
            var nodePair = split(root, e);
            var newLeft = merge(new NodePair(nodePair.left, new Node(e)));
            var newRight = nodePair.right;
            root = merge(new NodePair(newLeft, newRight));
            return true;
        }
    }

    private class TreapIterator implements Iterator<E> {
        private Node pointer;

        private TreapIterator() {
            pointer = root;
            if (pointer != null) {
                pointer.regulate(isDescending);
            }
            while (pointer != null && pointer.left != null) {
                pointer = pointer.left;
                pointer.regulate(isDescending);
            }
        }

        @Override
        public boolean hasNext() {
            return pointer != null;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Node prev = pointer;
            pointer = getNext(pointer);
            return prev.value;
        }

    }

    private Node getNext(@NotNull Node node) {
        node.regulate(isDescending);
        if(node.right != null) {
            node = node.right;
            node.regulate(isDescending);
            while(node.left != null) {
                node = node.left;
                node.regulate(isDescending);
            }
            return node;
        } else {
            while(node.direction.equals(Direction.RIGHT)) {
                node = node.ancestor;
                node.regulate(isDescending);
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

        nodePair.left.regulate(isDescending);
        nodePair.right.regulate(isDescending);

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

    private NodePair split(Node node, E e) {
        if(node == null) {
            return new NodePair(null, null);
        }

        node.regulate(isDescending);
        if(comparator.compare(node.value, e) < 0) {
            var split = split(node.right, e);
            node.right = split.left;
            if(node.right != null) {
                node.right.direction = Direction.RIGHT;
                node.right.ancestor = node;
            }
            node.direction = Direction.ROOT;
            node.update();
            return new NodePair(node, split.right);
        } else {
            var split = split(node.left, e);
            node.left = split.right;
            if(node.left != null) {
                node.left.direction = Direction.LEFT;
                node.left.ancestor = node;
            }
            node.direction = Direction.ROOT;
            node.update();
            return new NodePair(split.left, node);
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
        private boolean isReversed;

        private Node() {
            this(null);
        }

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

        private void regulate(boolean isReversed) {
            if(this.isReversed != isReversed) {
                var tmp = left;
                left = right;
                right = tmp;

                left.direction = toggle(left.direction);
                right.direction = toggle(right.direction);
                this.isReversed = isReversed;
            }

        }

        private void update() {
            size = size(left) + size(right) + 1;
        }

        private E lower(@NotNull E e) {
            regulate(isDescending);
            if(comparator.compare(value, e) < 0) {
                if(right == null) {
                    return value;
                } else {
                    return right.lower(e);
                }
            } else {
                if(left != null) {
                    return left.lower(e);
                } else {
                    return null;
                }
            }
        }

        private boolean contains(@NotNull E e) {
            regulate(isDescending);
            if(comparator.compare(value, e) == 0) {
                return true;
            }
            if(comparator.compare(value, e) < 0) {
                if(right == null) {
                    return false;
                } else {
                    return right.contains(e);
                }
            } else {
                if(left == null) {
                    return false;
                } else {
                    return left.contains(e);
                }
            }
        }
    }

    private int size(Node node) {
        return node == null ? 0 : node.size;
    }

    private class NodePair {
        private Node left;
        private Node right;

        private NodePair(Node left, Node right) {
            this.left = left;
            this.right = right;
        }
    }

    private enum Direction {
        LEFT, RIGHT, ROOT
    }

    private Direction toggle(Direction s) {
        if(s.equals(Direction.RIGHT)) {
            return Direction.LEFT;
        }
        if(s.equals(Direction.LEFT)) {
            return Direction.RIGHT;
        }
        return Direction.ROOT;
    }
}
