package ru.hse.kuzyaka.trie;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Serializable {
    /**
     * Writes this trie as a byte sequence
     *
     * @param out <code>OutputStream</code> to write trie to
     * @throws IOException
     */
    void serialize(OutputStream out) throws IOException;

    /**
     * Reads a trie from the given <code>InputStream</code>. Any data which the trie contained earlier, is discarded.
     *
     * @param in <code>InputStream</code> to read from
     * @throws IOException
     */
    void deserialize(InputStream in) throws IOException;
}
