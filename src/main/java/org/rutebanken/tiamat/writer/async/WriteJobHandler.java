package org.rutebanken.tiamat.writer.async;

/**
 * Processes a write job. Transport agnostic: whichever transport delivered the message, the work
 * and its correctness properties are identical, so they live here rather than in a transport
 * implementation where each new transport would have to reproduce them.
 */
public interface WriteJobHandler {

    void handle(WriteJobMessage message);
}
