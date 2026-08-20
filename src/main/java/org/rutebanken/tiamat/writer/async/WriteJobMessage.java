package org.rutebanken.tiamat.writer.async;

/**
 * A unit of asynchronous write work, as handed to a transport and given back to the handler.
 * <p>
 * The payload is the raw request body rather than an unmarshalled object graph, so that a
 * transport can carry it as-is and no parsing happens before the job is accepted. The operation
 * is carried as data rather than encoded in a method name, so that a transport does not have to
 * invent its own way of demultiplexing on the receiving side.
 */
public record WriteJobMessage(Long jobId, Operation operation, byte[] payload) {

    public enum Operation {
        CREATE,
        UPDATE,
        DELETE
    }

    public static WriteJobMessage create(Long jobId, byte[] payload) {
        return new WriteJobMessage(jobId, Operation.CREATE, payload);
    }

    public static WriteJobMessage update(Long jobId, byte[] payload) {
        return new WriteJobMessage(jobId, Operation.UPDATE, payload);
    }

    /**
     * Delete carries the stop place id as its payload, so that every operation has the same shape
     * on the wire.
     */
    public static WriteJobMessage delete(Long jobId, String stopPlaceId) {
        return new WriteJobMessage(jobId, Operation.DELETE, stopPlaceId.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    public String payloadAsString() {
        return new String(payload, java.nio.charset.StandardCharsets.UTF_8);
    }
}
