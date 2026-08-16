package org.rutebanken.tiamat.rest.write.async;

/**
 * Entry point for asynchronous stop place writes.
 * <p>
 * The payload is passed as raw bytes rather than as an unmarshalled object graph, so that no
 * parsing happens on the request thread before the job is accepted, and so that an
 * implementation backed by a message broker can carry the payload as-is.
 */
public interface StopPlaceAsyncProcessor {
    void processCreateStopPlace(Long jobId, byte[] payload);
    void processUpdateStopPlace(Long jobId, byte[] payload);
    void processDeleteStopPlace(Long jobId, String stopPlaceId);
}
