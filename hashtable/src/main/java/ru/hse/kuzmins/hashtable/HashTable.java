package ru.hse.kuzmins.hashtable;

import org.jetbrains.annotations.NotNull;

/**This class implements a hashtable which maps keys to values. Both values and keys are of type String
 * An instance of HashTable has one initial parameter -- its capacity.
 * The capacity is the number of possible hashes.
 * In case of hash collision data is stored in form of a list, thus finding and deleting both take linear time
 * in the worst case. */
public class HashTable {
    /** The number of stored keys */
    private int size = 0;
    /** The maximal number of keys which can be stored.
     * Note -- table is rebuild in case number of stored keys is greater the the half of its capacity. */
    private int capacity = 10;
    /** Boxes for storing pairs <code>(key, value)</code> */
    private LinkedList[] table;

    /**
     * Constructs an empty hashtable with given initial capacity.
     *
     * @param capacity the initial capacity of the hashtable
     */
    public HashTable(int capacity) {
        this.capacity = capacity;
        table = new LinkedList[capacity];
    }

    /** Constructs an empty hashtable with default initial capacity (10). */
    public HashTable() {
        table = new LinkedList[capacity];
    }

    /**
     * Calculates the hash of the string modulo mod
     *
     * @param key the string which hash is to be calculated
     * @param mod the modulo used in calculation
     * @return the hash of <code>key</code> modulo <code>mod</code>
     */
    private int getHash(@NotNull String key, int mod) {
        int hash = key.hashCode() % mod;
        if (hash < 0) {
            hash += mod;
        }
        return hash;
    }

    /** Rebuilds a hashtable if its size is greater than half of its capacity.
     * Rebuild is done via creating a new table with former size and capacity multiplied by 2.
     * All keys are rehashed modulo new capacity. */
    private void rebuild() {
        if (size * 2 < capacity)
            return;
        capacity *= 2;

        LinkedList[] newTable = new LinkedList[capacity];
        for (LinkedList list : table) {
            if (list == null || list.isEmpty()) {
                continue;
            }

            Object[] curBox = list.toArray();
            for (Object obj : curBox) {
                Data elem = (Data) obj;
                int hash = getHash(elem.key, capacity);

                if (newTable[hash] == null)
                    newTable[hash] = new LinkedList();
                newTable[hash].add(elem);
            }
        }
        table = newTable;
    }

    /**
     * Returns the number of keys in this hashtable.
     *
     * @return the number of keys in this hashtable
     */
    public int size() {
        return size;
    }

    /**
     * Checks if this table contains given key
     * @param key the key to search for
     * @return <code>true</code> if the key maps to some value; <code>false</code> otherwise
     */
    public boolean contains(@NotNull String key) {
        int hash = getHash(key, capacity);
        return table[hash] != null && table[hash].contains(new Data(key, null));
    }

    /**
     * Looks up the given key in this table
     * @param key a key to search for
     * @return value, stored by this key, if table contains such key; <code>null</code> otherwise
     */
    public String get(String key) {
        int hash = getHash(key, capacity);
        if (table[hash] == null) {
            return null;
        }

        int index = table[hash].indexOf(new Data(key, null));
        String res = null;
        if (index != -1) {
            res = ((Data) table[hash].get(index)).value;
        }
        return res;
    }

    /**
     * Maps the given key to the given value.
     * @param key key which is mapped to, mustn't be <cdde>null</cdde>
     * @param value value that is associated to the key, mustn't be <code>null</code>
     * @return the former stored value mapped by the key; <code>null</code> otherwise
     */
    public String put(@NotNull String key, @NotNull String value) {
        rebuild();

        int hash = getHash(key, capacity);
        if (table[hash] == null) {
            table[hash] = new LinkedList();
        }

        int index = table[hash].indexOf(new Data(key, null));
        String res = null;
        if (index == -1) {
            size++;
            table[hash].add(new Data(key, value));
        } else {
            res = ((Data) table[hash].get(index)).value;
            table[hash].set(index, new Data(key, value));
        }

        return res;
    }

    /**
     * Removes the key from this table
     * @param key the key that needs to be removed
     * @return the former stored value mapped by the key; <code>null</code> otherwise
     */
    public String remove(@NotNull String key) {
        int hash = getHash(key, capacity);
        if (table[hash] == null)
            return null;

        String res = null;
        int index = table[hash].indexOf(new Data(key, null));
        if (index != -1) {
            size--;
            res = ((Data) table[hash].get(index)).value;
            table[hash].remove(index);
        }

        return res;
    }

    /** Clears this table, so that it contains no keys */
    public void clear() {
        for (LinkedList list : table) {
            if (list == null || list.isEmpty())
                continue;
            list.clear();
        }
        size = 0;
        capacity = 10;
    }

    /**
     * Implements class for storing pairs <code>(Key, Value)</code>.
     */
    private class Data {
        /** key stored in this Data instance. */
        private String key;
        /** value stored int this Data instance. */
        private String value;

        /**
         * Constructs an instance of Data with specified key and value.
         * @param key key associated to this instance
         * @param value value associated to this instance
         */
        private Data(String key, String value) {
            this.key = key;
            this.value = value;
        }

        /**
         * {@inheritDoc}
         * @param other the object to be compared with
         */
        @Override
        public boolean equals(Object other) {
            if (this == other)
                return true;

            if (!(other instanceof Data))
                return false;

            Data otherObj = (Data) other;
            return key.equals(otherObj.key);
        }

        /**
         * {@inheritDoc}
         * @return hash of the key
         */
        @Override
        public int hashCode() {
            return key.hashCode();
        }
    }
}