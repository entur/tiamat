package org.rutebanken.tiamat.ext.fintraffic.netex;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.tiamat.importer.ImportParams;
import org.rutebanken.tiamat.importer.ImportType;
import org.rutebanken.tiamat.importer.PublicationDeliveryImporter;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryUnmarshaller;
import org.rutebanken.tiamat.service.BlobStoreService;
import org.springframework.boot.DefaultApplicationArguments;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@ExtendWith(MockitoExtension.class)
class NetexImportTaskTest {

    @Mock
    private BlobStoreService blobStoreService;
    @Mock
    private PublicationDeliveryUnmarshaller unmarshaller;
    @Mock
    private PublicationDeliveryImporter importer;

    private final DefaultApplicationArguments noArgs = new DefaultApplicationArguments();

    @Test
    void missingS3Key_exitsWithCode1() {
        var result = runWith(null, null);

        assertThat(result.exitCodes).containsExactly(1);
        verifyNoInteractions(blobStoreService, unmarshaller, importer);
    }

    @Test
    void blankS3Key_exitsWithCode1() {
        var result = runWith("   ", null);

        assertThat(result.exitCodes).containsExactly(1);
        verifyNoInteractions(blobStoreService, unmarshaller, importer);
    }

    @Test
    void happyPath_callsImporterAndExitsWithCode0() throws Exception {
        String s3Key = "netex/farezones.xml";
        InputStream fakeStream = new ByteArrayInputStream(new byte[0]);
        PublicationDeliveryStructure delivery = new PublicationDeliveryStructure();

        when(blobStoreService.download(s3Key)).thenReturn(fakeStream);
        when(unmarshaller.unmarshal(fakeStream)).thenReturn(delivery);

        var result = runWith(s3Key, null);

        ArgumentCaptor<ImportParams> paramsCaptor = ArgumentCaptor.forClass(ImportParams.class);
        verify(importer).importPublicationDelivery(eq(delivery), paramsCaptor.capture());
        assertThat(paramsCaptor.getValue().importType).isEqualTo(ImportType.INITIAL);
        assertThat(result.exitCodes).containsExactly(0);
        verify(blobStoreService).upload(eq(NetexImportTask.STATUS_S3_KEY), any());
    }

    @Test
    void customImportType_passedToImporter() throws Exception {
        String s3Key = "netex/stops.xml";
        InputStream fakeStream = new ByteArrayInputStream(new byte[0]);
        PublicationDeliveryStructure delivery = new PublicationDeliveryStructure();

        when(blobStoreService.download(s3Key)).thenReturn(fakeStream);
        when(unmarshaller.unmarshal(fakeStream)).thenReturn(delivery);

        var result = runWith(s3Key, "MERGE");

        ArgumentCaptor<ImportParams> paramsCaptor = ArgumentCaptor.forClass(ImportParams.class);
        verify(importer).importPublicationDelivery(eq(delivery), paramsCaptor.capture());
        assertThat(paramsCaptor.getValue().importType).isEqualTo(ImportType.MERGE);
        assertThat(result.exitCodes).containsExactly(0);
        verify(blobStoreService).upload(eq(NetexImportTask.STATUS_S3_KEY), any());
    }

    @Test
    void unknownImportType_defaultsToInitial() throws Exception {
        String s3Key = "netex/stops.xml";
        InputStream fakeStream = new ByteArrayInputStream(new byte[0]);
        PublicationDeliveryStructure delivery = new PublicationDeliveryStructure();

        when(blobStoreService.download(s3Key)).thenReturn(fakeStream);
        when(unmarshaller.unmarshal(fakeStream)).thenReturn(delivery);

        var result = runWith(s3Key, "NOT_A_REAL_TYPE");

        ArgumentCaptor<ImportParams> paramsCaptor = ArgumentCaptor.forClass(ImportParams.class);
        verify(importer).importPublicationDelivery(eq(delivery), paramsCaptor.capture());
        assertThat(paramsCaptor.getValue().importType).isEqualTo(ImportType.INITIAL);
        assertThat(result.exitCodes).containsExactly(0);
        verify(blobStoreService).upload(eq(NetexImportTask.STATUS_S3_KEY), any());
    }

    @Test
    void s3ObjectNotFound_exitsWithCode1() {
        when(blobStoreService.download(any())).thenReturn(null);

        var result = runWith("netex/missing.xml", null);

        assertThat(result.exitCodes).containsExactly(1);
        verifyNoInteractions(unmarshaller, importer);
        verifyStatusWritten(NetexImportTask.STATUS_FAILED);
    }

    @Test
    void importerThrows_exitsWithCode1() throws Exception {
        String s3Key = "netex/bad.xml";
        InputStream fakeStream = new ByteArrayInputStream(new byte[0]);
        PublicationDeliveryStructure delivery = new PublicationDeliveryStructure();

        when(blobStoreService.download(s3Key)).thenReturn(fakeStream);
        when(unmarshaller.unmarshal(fakeStream)).thenReturn(delivery);
        doThrow(new RuntimeException("import failed")).when(importer)
                .importPublicationDelivery(any(), any());

        var result = runWith(s3Key, null);

        assertThat(result.exitCodes).containsExactly(1);
        verifyStatusWritten(NetexImportTask.STATUS_FAILED);
    }

    @Test
    void unmarshallerThrows_exitsWithCode1() throws Exception {
        String s3Key = "netex/corrupt.xml";
        InputStream fakeStream = new ByteArrayInputStream(new byte[0]);

        when(blobStoreService.download(s3Key)).thenReturn(fakeStream);
        when(unmarshaller.unmarshal(fakeStream)).thenThrow(new RuntimeException("bad XML"));

        var result = runWith(s3Key, null);

        assertThat(result.exitCodes).containsExactly(1);
        verifyNoInteractions(importer);
        verifyStatusWritten(NetexImportTask.STATUS_FAILED);
    }

    @Test
    void successfulImport_writesDoneStatus() throws Exception {
        String s3Key = "netex/stops.xml";
        InputStream fakeStream = new ByteArrayInputStream(new byte[0]);
        when(blobStoreService.download(s3Key)).thenReturn(fakeStream);
        when(unmarshaller.unmarshal(fakeStream)).thenReturn(new PublicationDeliveryStructure());

        runWith(s3Key, null);

        verifyStatusWritten(NetexImportTask.STATUS_DONE);
    }

    @Test
    void failedImport_writesFailedStatus() throws Exception {
        String s3Key = "netex/stops.xml";
        InputStream fakeStream = new ByteArrayInputStream(new byte[0]);
        when(blobStoreService.download(s3Key)).thenReturn(fakeStream);
        when(unmarshaller.unmarshal(fakeStream)).thenReturn(new PublicationDeliveryStructure());
        doThrow(new RuntimeException("boom")).when(importer).importPublicationDelivery(any(), any());

        runWith(s3Key, null);

        verifyStatusWritten(NetexImportTask.STATUS_FAILED);
    }

    @Test
    void localFilePath_readsFromDiskAndSkipsStatusWrite(@TempDir java.nio.file.Path tempDir) throws Exception {
        java.nio.file.Path file = tempDir.resolve("stops.xml");
        java.nio.file.Files.write(file, "<xml/>".getBytes());
        String localPath = file.toAbsolutePath().toString();
        PublicationDeliveryStructure delivery = new PublicationDeliveryStructure();

        when(unmarshaller.unmarshal(any())).thenReturn(delivery);

        var result = runWith(localPath, null);

        assertThat(result.exitCodes).containsExactly(0);
        verify(blobStoreService, never()).download(any());
        verify(blobStoreService, never()).upload(any(), any());
        verify(importer).importPublicationDelivery(eq(delivery), any());
    }

    @Test
    void localFilePath_missingFile_exitsWithCode1(@TempDir java.nio.file.Path tempDir) {
        String localPath = tempDir.resolve("nonexistent.xml").toAbsolutePath().toString();

        var result = runWith(localPath, null);

        assertThat(result.exitCodes).containsExactly(1);
        verify(blobStoreService, never()).download(any());
        verify(blobStoreService, never()).upload(any(), any());
        verifyNoInteractions(unmarshaller, importer);
    }

    @Test
    void fileUrlScheme_readsFromDiskAndSkipsStatusWrite(@TempDir java.nio.file.Path tempDir) throws Exception {
        java.nio.file.Path file = tempDir.resolve("stops.xml");
        java.nio.file.Files.write(file, "<xml/>".getBytes());
        String fileUrl = "file://" + file.toAbsolutePath();
        PublicationDeliveryStructure delivery = new PublicationDeliveryStructure();

        when(unmarshaller.unmarshal(any())).thenReturn(delivery);

        var result = runWith(fileUrl, null);

        assertThat(result.exitCodes).containsExactly(0);
        verify(blobStoreService, never()).download(any());
        verify(blobStoreService, never()).upload(any(), any());
        verify(importer).importPublicationDelivery(eq(delivery), any());
    }

    @Test
    void isLocalPath_detectsAbsoluteAndRelative() {
        assertThat(NetexImportTask.isLocalPath("/tmp/stops.xml")).isTrue();
        assertThat(NetexImportTask.isLocalPath("./stops.xml")).isTrue();
        assertThat(NetexImportTask.isLocalPath("../stops.xml")).isTrue();
        assertThat(NetexImportTask.isLocalPath("file:///tmp/stops.xml")).isTrue();
        assertThat(NetexImportTask.isLocalPath("netex/stops.xml")).isFalse();
        assertThat(NetexImportTask.isLocalPath("s3://bucket/key")).isFalse();
    }

    // --- helpers ---

    private record RunResult(List<Integer> exitCodes) {}

    private void verifyStatusWritten(String expectedStatus) {
        ArgumentCaptor<InputStream> streamCaptor = ArgumentCaptor.forClass(InputStream.class);
        try {
            verify(blobStoreService).upload(eq(NetexImportTask.STATUS_S3_KEY), streamCaptor.capture());
            String written = new String(streamCaptor.getValue().readAllBytes(), StandardCharsets.UTF_8);
            assertThat(written).isEqualTo(expectedStatus);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private RunResult runWith(String s3Key, String importType) {
        List<Integer> exitCodes = new ArrayList<>();
        NetexImportTask task = new NetexImportTask(blobStoreService, unmarshaller, importer, exitCodes::add) {
            @Override
            protected String getenv(String name) {
                return switch (name) {
                    case ENV_S3_KEY -> s3Key;
                    case ENV_IMPORT_TYPE -> importType;
                    default -> null;
                };
            }
        };
        try {
            task.run(noArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new RunResult(exitCodes);
    }
}
