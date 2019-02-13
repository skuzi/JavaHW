package ru.hse.kuzyaka.trie;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.HashMap;

/**
 * Data structure for storing set of strings. Basic operations have the complexity of O(|length|).
 */
public class Trie implements Serializable {
    /** Root node which represents empty string. */
    private TrieNode root = new TrieNode();

    /**
     * Returns the size of the trie (the number of stored strings)
     *
     * @return the number of stored strings
     */
    public int size() {
        return root.terminalsInSubtree;
    }

    /**
     * Checks if the given string is in this trie
     *
     * @param element the string which is looked for
     * @return <code>true</code> if the trie contains this string; <code>false</code> otherwise
     */
    public boolean contains(@NotNull String element) {
        var lastNode = root.moveWithoutAdd(element);
        return lastNode.isTerminal && lastNode.depth == element.length();
    }

    /**
     * Adds the given string to this trie if it wasn't already in it
     *
     * @param element the string which is added
     * @return <code>true</code> if adding was successful; <code>false</code> otherwise
     */
    public boolean add(@NotNull String element) {
        var lastNode = root.moveWithAdd(element);
        var res = false;

        if (!lastNode.isTerminal) {
            lastNode.makeTerminal();
            res = true;
        }

        return res;
    }

    /**
     * Removes the given string from this trie if the trie actually contains it
     *
     * @param element the string which is removed
     * @return <code>true</code> if removal was successful; <code>false</code> otherwise
     */
    public boolean remove(@NotNull String element) {
        var lastNode = root.moveWithoutAdd(element);
        var res = false;

        if (lastNode.isTerminal && lastNode.depth == element.length()) {
            lastNode.makeNotTerminal();
            res = true;
        }

        return res;
    }

    /**
     * Tells how many strings in this trie start with the given prefix
     *
     * @param prefix the common prefix of all counted strings
     * @return the number of strings in this trie starting with the prefix
     */
    public int howManyStartsWithPrefix(@NotNull String prefix) {
        var lastNode = root.moveWithoutAdd(prefix);
        if (lastNode.depth == prefix.length()) {
            return lastNode.terminalsInSubtree;
        } else {
            return 0;
        }
    }

    /**
     * Writes this trie as a byte sequence
     *
     * @param out <code>OutputStream</code> to write trie to
     * @throws IOException
     */
    @Override
    public void serialize(@NotNull OutputStream out) throws IOException {
        var dataOut = new DataOutputStream(out);
        root.serializeSubtree(dataOut);
        dataOut.flush();
    }

    /**
     * Reads a trie from the given <code>InputStream</code>. Any data which the trie contained earlier, is discarded.
     *
     * @param in <code>InputStream</code> to read from
     * @throws IOException
     */
    @Override
    public void deserialize(@NotNull InputStream in) throws IOException {
        var dataIn = new DataInputStream(in);
        root = new TrieNode();
        root.deserializeSubtree(dataIn);
    }

    /**
     * Checks if this trie is equal to another object (other object must be instance of {@code Trie}).
     * Equality of tries means that they contain the same set of strings, i.e.
     * every string from first trie presents in second, and vice-versa
     *
     * @param o object to be compared with
     * @return {@code true} if this tree is equal to other trie
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Trie)) {
            return false;
        }
        var other = (Trie) o;
        return subtreeEquals(root, other.root);
    }

    /**
     * Returns hashcode of this trie (actually it is always 0)
     *
     * @return hashcode of this trie
     */
    @Override
    public int hashCode() {
        return 0;
    }

    private boolean subtreeEquals(TrieNode node1, TrieNode node2) {
        if (node1 == null && node2 == null) {
            return true;
        } else if (node1 != null && node2 != null) {
            boolean result = true;
            result &= node1.isTerminal == node2.isTerminal;
            result &= node1.depth == node2.depth;
            result &= node1.terminalsInSubtree == node2.terminalsInSubtree;
            result &= node1.next.size() == node2.next.size();
            for (var entry : node1.next.entrySet()) {
                if (!node2.next.containsKey(entry.getKey())) {
                    return false;
                }
                result &= subtreeEquals(entry.getValue(), node2.next.get(entry.getKey()));
            }
            return result;
        } else {
            return false;
        }
    }

    private class TrieNode {
        private boolean isTerminal;
        private int depth;
        private int terminalsInSubtree;
        private HashMap<Character, TrieNode> next;
        private @Nullable TrieNode parent;
        private char lastOnPath;

        private TrieNode() {
            this(0, null, '\0');
        }

        private TrieNode(int depth, @Nullable TrieNode parent, char lastOnPath) {
            this.depth = depth;
            this.parent = parent;
            this.lastOnPath = lastOnPath;
            next = new HashMap<>();
            terminalsInSubtree = 0;
            isTerminal = false;
        }

        private boolean hasNext(char c) {
            return next.containsKey(c);
        }

        private TrieNode getNext(char c) {
            if (!hasNext(c)) {
                next.put(c, new TrieNode(depth + 1, this, c));
            }
            return next.get(c);
        }

        private TrieNode moveWithAdd(@NotNull String element) {
            var curNode = this;
            for (char c : element.toCharArray()) {
                curNode = curNode.getNext(c);
            }
            return curNode;
        }

        private TrieNode moveWithoutAdd(@NotNull String element) {
            var curNode = this;
            for (char c : element.toCharArray()) {
                if (!curNode.hasNext(c))
                    break;
                curNode = curNode.getNext(c);
            }
            return curNode;
        }

        private void makeNotTerminal() {
            isTerminal = false;
            terminalsInSubtree--;

            var parentNode = parent;
            var node = this;
            while (parentNode != null) {
                parentNode.terminalsInSubtree--;

                if (node.terminalsInSubtree == 0) {
                    parentNode.next.remove(node.lastOnPath);
                }

                node = node.parent;
                parentNode = node.parent;
            }
        }

        private void makeTerminal() {
            isTerminal = true;

            var node = this;
            while (node != null) {
                node.terminalsInSubtree++;
                node = node.parent;
            }
        }

        private void serializeSubtree(@NotNull DataOutputStream out) throws IOException {
            out.writeBoolean(isTerminal);
            out.writeInt(terminalsInSubtree);
            out.writeInt(depth);
            out.writeChar(lastOnPath);
            out.writeInt(next.size());

            for (var entry : next.entrySet()) {
                out.writeChar(entry.getKey());
                entry.getValue().serializeSubtree(out);
            }
        }

        private void deserializeSubtree(@NotNull DataInputStream in) throws IOException {
            isTerminal = in.readBoolean();
            terminalsInSubtree = in.readInt();
            depth = in.readInt();
            lastOnPath = in.readChar();
            int nextSize = in.readInt();
            for (int i = 0; i < nextSize; i++) {
                char edgeSymbol = in.readChar();
                var child = new TrieNode();

                child.deserializeSubtree(in);

                next.put(edgeSymbol, child);
                child.parent = this;
            }
        }
    }
}
