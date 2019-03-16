package ru.hse.kuzyaka.trie;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;

/** Data structure for storing set of strings. Basic operations have the complexity of O(|length|). */
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
     * Checks if this trie is equal to another object (other object must be instance of {@code Trie}).
     * Equality of tries means that they contain the same set of strings, i.e.
     * every string from first trie presents in second, and vice-versa
     *
     * @param o object to be compared with
     * @return {@code true} if this tree is equal to other trie
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Trie trie = (Trie) o;
        return Objects.equals(root, trie.root);
    }

    /**
     * Returns hashcode of this trie
     *
     * @return hashcode of this trie
     */
    @Override
    public int hashCode() {
        return Objects.hash(root);
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
        var result = false;

        if (!lastNode.isTerminal) {
            lastNode.makeTerminal();
            result = true;
        }

        return result;
    }

    /**
     * Removes the given string from this trie if the trie actually contains it
     *
     * @param element the string which is removed
     * @return <code>true</code> if removal was successful; <code>false</code> otherwise
     */
    public boolean remove(@NotNull String element) {
        var lastNode = root.moveWithoutAdd(element);
        var result = false;

        if (lastNode.isTerminal && lastNode.depth == element.length()) {
            lastNode.makeNotTerminal();
            result = true;
        }

        return result;
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
     * @throws IOException if an I/O error occurs
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
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void deserialize(@NotNull InputStream in) throws IOException {
        var dataIn = new DataInputStream(in);
        root = new TrieNode();
        root.deserializeSubtree(dataIn);
    }

    private class TrieNode {
        private boolean isTerminal;
        private int depth;
        private int terminalsInSubtree;
        private HashMap<Character, TrieNode> childrenNodes;
        private @Nullable TrieNode parent;
        private char lastOnPath;

        private TrieNode() {
            this(0, null, '\0');
        }

        private TrieNode(int depth, @Nullable TrieNode parent, char lastOnPath) {
            this.depth = depth;
            this.parent = parent;
            this.lastOnPath = lastOnPath;
            childrenNodes = new HashMap<>();
            terminalsInSubtree = 0;
            isTerminal = false;
        }

        private boolean hasNext(char c) {
            return childrenNodes.containsKey(c);
        }

        private TrieNode getNext(char c) {
            if (!hasNext(c)) {
                childrenNodes.put(c, new TrieNode(depth + 1, this, c));
            }
            return childrenNodes.get(c);
        }

        private TrieNode moveWithAdd(@NotNull String element) {
            var currentNode = this;
            for (char c : element.toCharArray()) {
                currentNode = currentNode.getNext(c);
            }
            return currentNode;
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
                    parentNode.childrenNodes.remove(node.lastOnPath);
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
            out.writeInt(childrenNodes.size());

            for (var entry : childrenNodes.entrySet()) {
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

                childrenNodes.put(edgeSymbol, child);
                child.parent = this;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TrieNode trieNode = (TrieNode) o;
            return isTerminal == trieNode.isTerminal &&
                    depth == trieNode.depth &&
                    terminalsInSubtree == trieNode.terminalsInSubtree &&
                    lastOnPath == trieNode.lastOnPath &&
                    Objects.equals(childrenNodes, trieNode.childrenNodes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(isTerminal, depth, terminalsInSubtree, childrenNodes, lastOnPath);
        }
    }
}
