package ru.hse.kuzyaka.trie;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.HashMap;

/** Data structure for storing set of strings. Basic operations have the complexity of O(|length|). */
public class Trie {
    /** Root node which represents empty string. */
    private TrieNode root;

    /**Constructs empty trie with no strings added. */
    public Trie() {
        root = new TrieNode();
    }

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
     * @param element the string which is looked for; mustn't be <code>null</code>
     * @return <code>true</code> if the trie contains this string; <code>false</code> otherwise
     */
    public boolean contains(@NotNull String element) {
        var lastNode = root.moveWithoutAdd(element);
        return lastNode.isTerminal && lastNode.depth == element.length();
    }

    /**
     * Adds the given string to this trie if it wasn't already in it
     *
     * @param element the string which is added, mustn't be <code>null</code>
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
     * @param element the string which is removed, mustn't be <code>null</code>
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
    int howManyStartsWithPrefix(@NotNull String prefix) {
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
    void serialize(@NotNull OutputStream out) throws IOException {
        var dataOut = new DataOutputStream(out);
        root.serializeSubtree(dataOut);
        dataOut.flush();
        dataOut.close();
    }

    /**
     * Reads a trie from the given <code>InputStream</code>. Any data which the trie contained earlier, is discarded.
     *
     * @param in <code>InputStream</code> to read from
     * @throws IOException
     */
    void deserialize(@NotNull InputStream in) throws IOException {
        var dataIn = new DataInputStream(in);
        root = new TrieNode();
        root.deserializeSubtree(dataIn);
        in.close();
    }

    /**Class representing nodes of the trie. */
    private class TrieNode {
        /** Tells, does any string end in this node. */
        private boolean isTerminal;
        /** Number of characters on the path to the root. */
        private int depth;
        /** Number of string ending in the subtree of this node. */
        private int terminalsInSubtree;
        /** Map of transitions char-node from this node, excluding the parent node. */
        private HashMap<Character, TrieNode> next;
        /** Reference to the parent node. */
        private @Nullable TrieNode parent;
        /** Character on the edge to the parent. */
        private char lastOnPath;

        /** Default constructor for nodes without parents (used only for root node)*/
        private TrieNode() {
            this(0, null, '\0');
        }

        /**
         * Constructs node with given depth, parent node and character on the edge to the parent
         *
         * @param depth depth value
         * @param parent reference to parent
         * @param lastOnPath character on the edge to the parent
         */
        private TrieNode(int depth, @Nullable TrieNode parent, char lastOnPath) {
            this.depth = depth;
            this.parent = parent;
            this.lastOnPath = lastOnPath;
            next = new HashMap<>();
            terminalsInSubtree = 0;
            isTerminal = false;
        }

        /**
         * Checks if given character exists on some edge from this node, excluding edge to the parent
         *
         * @param c character which transition is looked for
         * @return <code>true</code> if there is such transition; <code>false</code> otherwise
         */
        private boolean hasNext(char c) {
            return next.containsKey(c);
        }

        /**
         * Adds the transition by the given character from this node if there was no such
         *
         * @param c character which transition is looked for
         * @return node which represents the given char (if there was no such transition, new node is made)
         */
        private TrieNode getNext(char c) {
            if (!hasNext(c)) {
                next.put(c, new TrieNode(depth + 1, this, c));
            }
            return next.get(c);
        }

        /**
         * Looks for given string in the subtree of this node. New nodes are made if needed.
         *
         * @param element string to look for
         * @return node representing given string in the subtree of this node.
         */
        private TrieNode moveWithAdd(@NotNull String element) {
            var curNode = this;
            for (char c : element.toCharArray()) {
                curNode = curNode.getNext(c);
            }
            return curNode;
        }

        /**
         * Looks for given string in the subtree of this node. No new nodes are added.
         *
         * @param element string to look for
         * @return node where the last existing transition ended
         */
        private TrieNode moveWithoutAdd(@NotNull String element) {
            var curNode = this;
            for (char c : element.toCharArray()) {
                if (!curNode.hasNext(c))
                    break;
                curNode = curNode.getNext(c);
            }
            return curNode;
        }

        /** Makes this node not terminal (only if it was terminal).
         * Recalculates all ancestors so that the trie remains valid. */
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

        /** Makes this node terminal (only if it was not terminal).
         * Recalculates all ancestors so that the trie remains valid. */
        private void makeTerminal() {
            isTerminal = true;

            var node = this;
            while (node != null) {
                node.terminalsInSubtree++;
                node = node.parent;
            }
        }

        /**
         * Writes the subtree of this node to the given <code>DataOutputStream</code> as a byte sequence
         *
         * @param out <code>DataOutputStream</code> to write to, mustn't be <code>null</code>
         * @throws IOException
         */
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

        /**
         * Reads the subtree of this node from the given <code>DataInputStream</code>
         * Any old data of this node is discarded.
         * @param in <code>DataInputStream</code> to read from, mustn't be <code>null</code>
         * @throws IOException
         */
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
