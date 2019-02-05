package ru.hse.kuzyaka.trie;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.HashMap;

public class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public int size() {
        return root.terminalsInSubtree;
    }

    public boolean contains(@NotNull String element) {
        var lastNode = root.moveWithoutAdd(element);
        return lastNode.isTerminal && lastNode.depth == element.length();
    }

    public boolean add(@NotNull String element) {
        var lastNode = root.moveWithAdd(element);
        var res = false;

        if (!lastNode.isTerminal) {
            lastNode.makeTerminal();
            res = true;
        }

        return res;
    }

    public boolean remove(@NotNull String element) {
        var lastNode = root.moveWithoutAdd(element);
        var res = false;

        if (lastNode.isTerminal && lastNode.depth == element.length()) {
            lastNode.makeNotTerminal();
            res = true;
        }

        return res;
    }

    int howManyStartsWithPrefix(@NotNull String prefix) {
        var lastNode = root.moveWithoutAdd(prefix);
        if (lastNode.depth == prefix.length()) {
            return lastNode.terminalsInSubtree;
        } else {
            return 0;
        }
    }

    void serialize(@NotNull OutputStream out) throws IOException {
        var dataOut = new DataOutputStream(out);
        root.serializeSubtree(dataOut);
        dataOut.flush();
        dataOut.close();
    }

    void deserialize(@NotNull InputStream in) throws IOException {
        var dataIn = new DataInputStream(in);
        root = new TrieNode();
        root.deserializeSubtree(dataIn);
        in.close();
    }


    private class TrieNode {
        private boolean isTerminal;
        private int depth;
        private int terminalsInSubtree;
        private HashMap<Character, TrieNode> next;
        private @Nullable TrieNode father;
        private char lastOnPath;

        private TrieNode() {
            this(0, null, '\0');
        }

        private TrieNode(int depth, @Nullable TrieNode father, char lastOnPath) {
            this.depth = depth;
            this.father = father;
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

            var fatherNode = father;
            var node = this;
            while (fatherNode != null) {
                fatherNode.terminalsInSubtree--;

                if (node.terminalsInSubtree == 0) {
                    fatherNode.next.remove(node.lastOnPath);
                }

                node = node.father;
                fatherNode = node.father;
            }
        }

        private void makeTerminal() {
            isTerminal = true;

            var node = this;
            while (node != null) {
                node.terminalsInSubtree++;
                node = node.father;
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
                var son = new TrieNode();

                son.deserializeSubtree(in);

                next.put(edgeSymbol, son);
                son.father = this;
            }
        }
    }
}
