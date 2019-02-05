package ru.hse.kuzyaka.trie;

import org.jetbrains.annotations.NotNull;

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

        if(!lastNode.isTerminal) {
            lastNode.makeTerminal();
            res = true;
        }

        return res;
    }

    public boolean remove(@NotNull String element) {
        var lastNode = root.moveWithoutAdd(element);
        var res = false;

        if(lastNode.isTerminal && lastNode.depth == element.length()) {
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



    private class TrieNode {
        private boolean isTerminal;
        private int depth;
        private int terminalsInSubtree;
        private HashMap<Character, TrieNode> next;
        private TrieNode father;
        private char lastOnPath;

        private TrieNode() {
            this(0, null, '\0');
        }

        private TrieNode(int depth, TrieNode father, char lastOnPath) {
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
            if(!hasNext(c)) {
                next.put(c, new TrieNode(depth + 1, this, c));
            }
            return next.get(c);
        }

        private TrieNode moveWithAdd(String element) {
            var curNode = this;
            for(char c : element.toCharArray()) {
                curNode = curNode.getNext(c);
            }
            return curNode;
        }

        private TrieNode moveWithoutAdd(String element) {
            var curNode = this;
            for (char c : element.toCharArray()) {
                if(!curNode.hasNext(c))
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
            while(fatherNode != null) {
                fatherNode.terminalsInSubtree--;

                if(node.terminalsInSubtree == 0) {
                    fatherNode.next.remove(node.lastOnPath);
                }

                node = node.father;
                fatherNode = node.father;
            }
        }

        private void makeTerminal() {
            isTerminal = true;

            var node = this;
            while(node != null) {
                node.terminalsInSubtree++;
                node = node.father;
            }
        }
    }
}
