package org.rutebanken.tiamat.rest.write.async;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rutebanken.tiamat.rest.write.JobService;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultWriteJobHandlerTest {

    private static final Long JOB_ID = 42L;
    private static final byte[] PAYLOAD = "<stopPlaces/>".getBytes(StandardCharsets.UTF_8);

    @Mock
    private JobService jobService;

    @Mock
    private WriteJobProcessor processor;

    private DefaultWriteJobHandler handler;

    @BeforeEach
    void setup() {
        handler = new DefaultWriteJobHandler(jobService, processor);
    }

    /**
     * Delivery is at least once and processing is not idempotent, so a redelivery of a job that
     * has already been claimed or finished must do nothing at all. A create that ran twice would
     * mint a second NSR id and silently produce a duplicate stop place.
     */
    @Test
    void doesNothingWhenTheJobCannotBeClaimed() {
        when(jobService.claim(JOB_ID)).thenReturn(false);

        handler.handle(WriteJobMessage.create(JOB_ID, PAYLOAD));

        verifyNoInteractions(processor);
        verify(jobService, never()).fail(any(), any());
    }

    @Test
    void processesTheJobOnceItIsClaimed() {
        when(jobService.claim(JOB_ID)).thenReturn(true);
        WriteJobMessage message = WriteJobMessage.create(JOB_ID, PAYLOAD);

        handler.handle(message);

        verify(processor).process(message);
    }

    /**
     * The write and its completion are one transaction, which has rolled back by the time the
     * exception reaches here. Recording the failure therefore has to happen outside it, which is
     * why the handler catches rather than the processor.
     */
    @Test
    void recordsTheFailureWhenProcessingThrows() {
        when(jobService.claim(JOB_ID)).thenReturn(true);
        RuntimeException failure = new RuntimeException("write failed");
        doThrow(failure).when(processor).process(any(WriteJobMessage.class));

        handler.handle(WriteJobMessage.create(JOB_ID, PAYLOAD));

        verify(jobService).fail(eq(JOB_ID), eq(failure));
    }

    /**
     * A lost claim is reported the same way: the write has already been rolled back by the
     * conditional completion, so the job simply records why.
     */
    @Test
    void recordsTheFailureWhenTheClaimWasLostDuringTheWrite() {
        when(jobService.claim(JOB_ID)).thenReturn(true);
        WriteJobNotOwnedException lost = new WriteJobNotOwnedException(JOB_ID);
        doThrow(lost).when(processor).process(any(WriteJobMessage.class));

        handler.handle(WriteJobMessage.create(JOB_ID, PAYLOAD));

        verify(jobService).fail(eq(JOB_ID), eq(lost));
    }
}
