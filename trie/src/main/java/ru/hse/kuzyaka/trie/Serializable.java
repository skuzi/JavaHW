package ru.hse.kuzyaka.trie;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface provides to work with specific data (send it or get it) in a similiar way as with bytestreams.
 * More specifically, it has two methods -- {@code deserialize(InputStream)} and {@code serialize(OutputStream)} which are used
 * to send and receive data. Any class implementing this interface must provide an implementation for both methods.
 * Note that if working with streams goes wrong, an {@code IOException} is thrown.
 */
public interface Serializable {
    /**
     * Writes this object as a byte sequence
     *
     * @param out <code>OutputStream</code> to write trie to
     * @throws IOException if an I/O error occurs
     */
    void serialize(OutputStream out) throws IOException;

    /**
     * Reads an object from the given <code>InputStream</code>. Any data which the object contained earlier, is discarded.
     *
     * @param in <code>InputStream</code> to read from
     * @throws IOException if an I/O error occurs
     */
    void deserialize(InputStream in) throws IOException;
}
