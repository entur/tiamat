package org.rutebanken.tiamat.rest.write.controllers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rutebanken.helper.organisation.AuthorizationConstants;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.auth.MockedRoleAssignmentExtractor;
import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.model.AccessibilityLimitation;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.ValidBetween;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.rest.write.dto.StopPlaceJobDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestRestTemplate
public class StopPlaceControllerIntegrationTest extends TiamatIntegrationTest {

    @Autowired
    private MockedRoleAssignmentExtractor mockedRoleAssignmentExtractor;

    @Before
    public void setUpPersistentRoleAssignment() {
        RoleAssignment role = RoleAssignment.builder()
            .withRole("editStops")
            .withOrganisation("*")
            .withEntityClassification(AuthorizationConstants.ENTITY_TYPE, AuthorizationConstants.ENTITY_CLASSIFIER_ALL_TYPES)
            .build();
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(role);
        mockedRoleAssignmentExtractor.setPersistent(true);
    }

    @After
    public void tearDownPersistentRoleAssignment() {
        mockedRoleAssignmentExtractor.reset();
    }

    private static final String WRITE_ENDPOINT = "/services/stop_places/write";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void createStopPlaceReturnsAcceptedWithProcessingJob() throws InterruptedException {
        String xml = """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace version="1">
                    <Name>Test Station</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                    <quays>
                        <Quay id="NSR:Quay:123" version="666">
                            <Name>Quay 1</Name>
                        </Quay>
                    </quays>
                </StopPlace>
            </stopPlaces>
            """;

        ResponseEntity<StopPlaceJobDto> response = postXml(
            xml,
            StopPlaceJobDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().jobId()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(
            AsyncStopPlaceJobStatus.PROCESSING
        );

        Long jobId = response.getBody().jobId();
        StopPlaceJobDto finalJob = awaitJobCompletion(jobId);

        assertThat(finalJob.status()).isEqualTo(AsyncStopPlaceJobStatus.FINISHED);
    }

    @Test
    public void createStopPlaceWithMultipleStopPlacesReturnsFailedJob()
        throws InterruptedException {
        String xml = """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace version="1">
                    <Name>Station A</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                </StopPlace>
                <StopPlace version="1">
                    <Name>Station B</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                </StopPlace>
            </stopPlaces>
            """;

        ResponseEntity<StopPlaceJobDto> response = postXml(
            xml,
            StopPlaceJobDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().jobId()).isNotNull();

        Long jobId = response.getBody().jobId();
        StopPlaceJobDto finalJob = awaitJobCompletion(jobId);

        assertThat(finalJob.status()).isEqualTo(AsyncStopPlaceJobStatus.FAILED);
        assertThat(finalJob.errorMessage()).contains(
            "Invalid stop place structure."
        );
    }

    /**
     * The payload is not parsed on the request thread, so a malformed body is accepted with 202
     * and reported as a failed job rather than as a 400.
     */
    @Test
    public void createStopPlaceWithMalformedXmlReturnsFailedJob() throws InterruptedException {
        String xml = """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace version="1">
                    <Name>Broken
            </stopPlaces>
            """;

        ResponseEntity<StopPlaceJobDto> response = postXml(xml, StopPlaceJobDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        StopPlaceJobDto finalJob = awaitJobCompletion(response.getBody().jobId());

        assertThat(finalJob.status()).isEqualTo(AsyncStopPlaceJobStatus.FAILED);
        assertThat(finalJob.errorMessage()).contains("Malformed XML");
    }

    @Test
    public void createStopPlaceWithUnsupportedXmlReturnsFailedJob() throws InterruptedException {
        String xml = """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace version="1">
                    <Name>Station A</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                    <notAValidTag>asd</notAValidTag>
                </StopPlace>
            </stopPlaces>
            """;

        ResponseEntity<StopPlaceJobDto> response = postXml(xml, StopPlaceJobDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        StopPlaceJobDto finalJob = awaitJobCompletion(response.getBody().jobId());

        assertThat(finalJob.status()).isEqualTo(AsyncStopPlaceJobStatus.FAILED);
        assertThat(finalJob.errorMessage()).contains("notAValidTag");
    }

    @Test
    public void deleteStopPlaceReturnsAcceptedWithProcessingJob() throws InterruptedException {
        StopPlace stopPlace = new StopPlace(
            new EmbeddableMultilingualString("To Be Deleted")
        );
        stopPlace.setValidBetween(
                new ValidBetween(Instant.now())
        );
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        StopPlace saved = stopPlaceRepository.save(stopPlace);

        ResponseEntity<StopPlaceJobDto> response = restTemplate.exchange(
            WRITE_ENDPOINT + "/" + saved.getNetexId(),
            HttpMethod.DELETE,
            null,
            StopPlaceJobDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().jobId()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(
            AsyncStopPlaceJobStatus.PROCESSING
        );

        Long jobId = response.getBody().jobId();
        StopPlaceJobDto finalJob = awaitJobCompletion(jobId);

        assertThat(finalJob.status()).isEqualTo(AsyncStopPlaceJobStatus.FINISHED);
    }

    /**
     * A database constraint has its own reason, distinct from the generic one, and it only helps if
     * the exception still matches by the time it reaches the job. It is raised deep in the write,
     * translated by Spring, and may be wrapped on its way out of the transactional proxy, so this
     * goes through the real endpoint rather than calling the formatter directly.
     * <p>
     * A name longer than the column is the cheapest violation to provoke deterministically, and it
     * stands in for the one that actually matters, which is two writers computing the same version.
     */
    @Test
    public void constraintViolationIsReportedWithItsOwnReason() throws InterruptedException {
        String tooLongName = "x".repeat(300);
        String xml = """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace version="1">
                    <Name>%s</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                </StopPlace>
            </stopPlaces>
            """.formatted(tooLongName);

        ResponseEntity<StopPlaceJobDto> response = postXml(xml, StopPlaceJobDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

        StopPlaceJobDto finalJob = awaitJobCompletion(response.getBody().jobId());

        assertThat(finalJob.status()).isEqualTo(AsyncStopPlaceJobStatus.FAILED);
        assertThat(finalJob.errorMessage())
                .as("a constraint violation must not degrade to the generic reason")
                .isEqualTo("A database constraint was violated. This may be due to invalid input data or a conflict with existing data.");
    }

    /**
     * The stop place is not looked up before the job is accepted, so an unknown id cannot be
     * answered with a 404. It has to come back as a failed job carrying a reason the caller can
     * act on, which is what the endpoint documentation promises.
     */
    @Test
    public void deleteUnknownStopPlaceReturnsAcceptedThenFailedJob() throws InterruptedException {
        ResponseEntity<StopPlaceJobDto> response = restTemplate.exchange(
            WRITE_ENDPOINT + "/NSR:StopPlace:doesNotExist",
            HttpMethod.DELETE,
            null,
            StopPlaceJobDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isNotNull();

        StopPlaceJobDto finalJob = awaitJobCompletion(response.getBody().jobId());

        assertThat(finalJob.status()).isEqualTo(AsyncStopPlaceJobStatus.FAILED);
        assertThat(finalJob.errorMessage()).contains("Cannot find stop place to terminate");
    }

    @Test
    public void updateStopPlaceReturnsAcceptedWithProcessingJob() throws InterruptedException {
        StopPlace stopPlace = new StopPlace(
            new EmbeddableMultilingualString("Original Name")
        );
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        var quay = new Quay();
        quay.setNetexId("NSR:Quay:123");
        quay.setVersion(1L);
        stopPlace.setQuays(Set.of(quay));
        StopPlace saved = stopPlaceRepository.save(stopPlace);

        String xml = String.format(
            """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace id="%s" version="%d">
                    <Name>Updated Name</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                    <quays>
                        <Quay id="NSR:Quay:123" version="666">
                            <Name>Quay 1</Name>
                        </Quay>
                    </quays>
                </StopPlace>
            </stopPlaces>
            """,
            saved.getNetexId(), saved.getVersion()
        );

        ResponseEntity<StopPlaceJobDto> response = putXml(xml, StopPlaceJobDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().jobId()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(
            AsyncStopPlaceJobStatus.PROCESSING
        );

        Long jobId = response.getBody().jobId();
        StopPlaceJobDto finalJob = awaitJobCompletion(jobId);

        assertThat(finalJob.status()).isEqualTo(AsyncStopPlaceJobStatus.FINISHED);
    }


    @Test
    public void updateStopPlaceWithUnsupportedXmlReturnsFailedJob() throws InterruptedException {
        StopPlace stopPlace = new StopPlace(
                new EmbeddableMultilingualString("Original Name")
        );
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        var quay = new Quay();
        quay.setNetexId("NSR:Quay:123");
        quay.setVersion(1L);
        stopPlace.setQuays(Set.of(quay));
        StopPlace saved = stopPlaceRepository.save(stopPlace);

        String xml = String.format(
                """
                <stopPlaces xmlns="http://www.netex.org.uk/netex">
                    <StopPlace id="%s" version="%d">
                        <Name>Updated Name</Name>
                        <StopPlaceType>busStation</StopPlaceType>
                        <quays>
                            <Quay id="NSR:Quay:123" version="666">
                                <Name>Quay 1</Name>
                                <NotAValidTag>asd</NotAValidTag>
                            </Quay>
                        </quays>
                    </StopPlace>
                </stopPlaces>
                """,
                saved.getNetexId(), saved.getVersion()
        );

        ResponseEntity<StopPlaceJobDto> response = putXml(xml, StopPlaceJobDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        StopPlaceJobDto finalJob = awaitJobCompletion(response.getBody().jobId());

        assertThat(finalJob.status()).isEqualTo(AsyncStopPlaceJobStatus.FAILED);
        assertThat(finalJob.errorMessage()).contains("NotAValidTag");
    }

    /**
     * The endpoint declares @Produces(application/xml), so the mapped error response is written
     * as XML. ErrorResponseEntity is @XmlRootElement annotated and relies on the JAXB message
     * body writer for that; without it Jersey cannot serialise the response and answers 500
     * instead of the mapped status.
     */


    @Test
    public void ignoresValidBetweenFields() throws InterruptedException {
        String xml = """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace version="666">
                    <ValidBetween>
                        <FromDate>2024-01-01</FromDate>
                        <ToDate>2024-12-31</ToDate>
                    </ValidBetween>
                    <Name>Evil Station</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                    <quays>
                        <Quay id="NSR:Quay:123" version="666">
                            <Name>Quay 1</Name>
                        </Quay>
                    </quays>
                </StopPlace>
            </stopPlaces>
            """;

        ResponseEntity<StopPlaceJobDto> response = postXml(
                xml,
                StopPlaceJobDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().jobId()).isNotNull();

        Long jobId = response.getBody().jobId();
        StopPlaceJobDto finalJob = awaitJobCompletion(jobId);

        assertThat(finalJob.createdIds()).isNotEmpty();

        String createdId = finalJob.createdIds().getFirst().createdId();

        Long version = written(createdId, StopPlace::getVersion);
        assertThat(version).isEqualTo(1L);
        Instant toDate = written(createdId, sp -> sp.getValidBetween() == null ? null : sp.getValidBetween().getToDate());
        assertThat(toDate).as("a client supplied validity must not be kept").isNull();
    }

    @Test
    public void createsImportedId() throws InterruptedException {
        String xml = """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace version="666" id="SAM:StopPlace:999">
                    <Name>Imported Station</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                    <quays>
                        <Quay id="SAM:Quay:123" version="666">
                            <Name>Quay 1</Name>
                        </Quay>
                    </quays>
                </StopPlace>
            </stopPlaces>
            """;

        ResponseEntity<StopPlaceJobDto> response = postXml(
                xml,
                StopPlaceJobDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().jobId()).isNotNull();

        Long jobId = response.getBody().jobId();
        StopPlaceJobDto finalJob = awaitJobCompletion(jobId);

        assertThat(finalJob.createdIds()).isNotEmpty();

        String createdId = finalJob.createdIds().getFirst().createdId();

        assertThat(createdId).isNotEqualTo("SAM:StopPlace:999");
        assertThat(written(createdId, StopPlace::getVersion)).isEqualTo(1L);
        Set<String> stopImportedIds = written(createdId, sp -> Set.copyOf(sp.getKeyValues().get("imported-id").getItems()));
        assertThat(stopImportedIds).contains("SAM:StopPlace:999");
        Set<String> quayImportedIds = written(createdId, sp -> Set.copyOf(sp.getQuays().iterator().next()
                .getKeyValues().get("imported-id").getItems()));
        assertThat(quayImportedIds).contains("SAM:Quay:123");
    }

    @Test
    public void ignoresQuayNetexIdAndVersion() throws InterruptedException {
        String xml = """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace version="1" id="SAM:StopPlace:1">
                    <Name>Station with quays</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                    <quays>
                        <Quay id="NSR:Quay:123" version="666">
                            <Name>Quay 1</Name>
                        </Quay>
                        <Quay id="NSR:Quay:99">
                            <Name>Quay 2</Name>
                        </Quay>
                    </quays>
                </StopPlace>
            </stopPlaces>
            """;

        ResponseEntity<StopPlaceJobDto> response = postXml(
                xml,
                StopPlaceJobDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().jobId()).isNotNull();

        Long jobId = response.getBody().jobId();
        StopPlaceJobDto finalJob = awaitJobCompletion(jobId);

        assertThat(finalJob.createdIds()).isNotEmpty();

        String createdId = finalJob.createdIds().getFirst().createdId();

        List<String> quayIds = written(createdId, sp -> sp.getQuays().stream().map(Quay::getNetexId).toList());
        assertThat(quayIds)
                .as("the register mints quay ids, it does not take the submitted ones")
                .containsExactlyInAnyOrder("NSR:Quay:1", "NSR:Quay:2");
        List<Long> quayVersions = written(createdId, sp -> sp.getQuays().stream().map(Quay::getVersion).toList());
        assertThat(quayVersions).containsOnly(1L);
    }

    @Test
    public void ignoresAccessibilityNetexIdAndVersion() throws InterruptedException {
        String xml = """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace version="1" id="NSR:StopPlace:1">
                    <Name>Station with quays</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                    <quays>
                        <Quay>
                            <Name>Quay 1</Name>
                        </Quay>
                    </quays>
                    <AccessibilityAssessment modification="new" version="667" id="NSR:AccessibilityAssessment:321">
                        <MobilityImpairedAccess>unknown</MobilityImpairedAccess>
                        <limitations>
                            <AccessibilityLimitation modification="new" version="668" id="NSR:AccessibilityLimitation:321">
                                <WheelchairAccess>unknown</WheelchairAccess>
                                <StepFreeAccess>unknown</StepFreeAccess>
                                <EscalatorFreeAccess>unknown</EscalatorFreeAccess>
                                <LiftFreeAccess>unknown</LiftFreeAccess>
                                <AudibleSignalsAvailable>unknown</AudibleSignalsAvailable>
                                <VisualSignsAvailable>unknown</VisualSignsAvailable>
                            </AccessibilityLimitation>
                        </limitations>
                    </AccessibilityAssessment>
                </StopPlace>
            </stopPlaces>
            """;

        ResponseEntity<StopPlaceJobDto> response = postXml(
                xml,
                StopPlaceJobDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().jobId()).isNotNull();

        Long jobId = response.getBody().jobId();
        StopPlaceJobDto finalJob = awaitJobCompletion(jobId);

        assertThat(finalJob.createdIds()).isNotEmpty();

        String createdId = finalJob.createdIds().getFirst().createdId();

        String assessmentId = written(createdId, sp -> sp.getAccessibilityAssessment().getNetexId());
        Long assessmentVersion = written(createdId, sp -> sp.getAccessibilityAssessment().getVersion());
        String limitationId = written(createdId, sp -> sp.getAccessibilityAssessment().getLimitations().getFirst().getNetexId());
        Long limitationVersion = written(createdId, sp -> sp.getAccessibilityAssessment().getLimitations().getFirst().getVersion());

        assertThat(assessmentId).as("the submitted accessibility id must be ignored").isEqualTo("NSR:AccessibilityAssessment:1");
        assertThat(assessmentVersion).isEqualTo(1L);
        assertThat(limitationId).isEqualTo("NSR:AccessibilityLimitation:1");
        assertThat(limitationVersion).isEqualTo(1L);
    }

    @Test
    public void shouldNotLetClientSetValidBetweenOnUpdate() throws InterruptedException {
        StopPlace stopPlace = new StopPlace(
            new EmbeddableMultilingualString("Original Name")
        );
        stopPlace.setValidBetween(
                new ValidBetween(Instant.parse("2024-01-01T00:00:00Z"), Instant.parse("2024-12-31T23:59:59Z"))
        );
        stopPlace.setVersion(1L);
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        var quay = new Quay();
        quay.setNetexId("NSR:Quay:123");
        quay.setVersion(1L);
        stopPlace.setQuays(Set.of(quay));
        StopPlace saved = stopPlaceRepository.save(stopPlace);

        String xml = String.format(
            """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace id="%s" version="%d">
                    <Name>Updated Name</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                    <ValidBetween>
                        <FromDate>2025-01-01T00:00:00</FromDate>
                        <ToDate>2025-12-31T23:59:59</ToDate>
                    </ValidBetween>
                    <quays>
                        <Quay id="NSR:Quay:123" version="666">
                            <Name>Quay 1</Name>
                        </Quay>
                    </quays>
                </StopPlace>
            </stopPlaces>
            """,
            saved.getNetexId(), saved.getVersion()
        );

        ResponseEntity<StopPlaceJobDto> response = putXml(xml, StopPlaceJobDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().jobId()).isNotNull();

        Long jobId = response.getBody().jobId();
        StopPlaceJobDto finalJob = awaitJobCompletion(jobId);

        assertThat(finalJob.status()).isEqualTo(AsyncStopPlaceJobStatus.FINISHED);

        Instant updatedToDate = written(saved.getNetexId(), sp -> sp.getValidBetween() == null ? null : sp.getValidBetween().getToDate());
        assertThat(updatedToDate).as("a client supplied validity must not be kept on update").isNull();
    }

    @Test
    public void shouldNotLetClientAffectVersionNumbersOnUpdate() throws InterruptedException {
        StopPlace stopPlace = new StopPlace(
            new EmbeddableMultilingualString("Original Name")
        );
        stopPlace.setVersion(1L);
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        var quay = new Quay();
        quay.setNetexId("NSR:Quay:123");
        quay.setVersion(1L);
        stopPlace.setQuays(Set.of(quay));
        var accessibility = new AccessibilityAssessment();
        var limitation = new AccessibilityLimitation();
        limitation.setVersion(1L);
        accessibility.setVersion(1L);
        accessibility.setLimitations(List.of(limitation));
        stopPlace.setAccessibilityAssessment(accessibility);
        StopPlace saved = stopPlaceRepository.save(stopPlace);

        String xml = String.format(
            """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace id="%s" version="%d">
                    <Name>Updated Name</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                    <AccessibilityAssessment modification="new" version="667" id="NSR:AccessibilityAssessment:1">
                        <MobilityImpairedAccess>unknown</MobilityImpairedAccess>
                        <limitations>
                            <AccessibilityLimitation modification="new" version="668" id="NSR:AccessibilityLimitation:1">
                                <WheelchairAccess>unknown</WheelchairAccess>
                                <StepFreeAccess>unknown</StepFreeAccess>
                                <EscalatorFreeAccess>unknown</EscalatorFreeAccess>
                                <LiftFreeAccess>unknown</LiftFreeAccess>
                                <AudibleSignalsAvailable>unknown</AudibleSignalsAvailable>
                                <VisualSignsAvailable>unknown</VisualSignsAvailable>
                            </AccessibilityLimitation>
                        </limitations>
                    </AccessibilityAssessment>
                    <quays>
                        <Quay id="NSR:Quay:123" version="666">
                            <Name>Quay 1</Name>
                        </Quay>
                    </quays>
                </StopPlace>
            </stopPlaces>
            """,
            saved.getNetexId(), saved.getVersion()
        );

        ResponseEntity<StopPlaceJobDto> response = putXml(xml, StopPlaceJobDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().jobId()).isNotNull();

        Long jobId = response.getBody().jobId();
        StopPlaceJobDto finalJob = awaitJobCompletion(jobId);

        assertThat(finalJob.status()).isEqualTo(AsyncStopPlaceJobStatus.FINISHED);

        Long updatedAssessmentVersion = written(saved.getNetexId(), sp -> sp.getAccessibilityAssessment().getVersion());
        String updatedAssessmentId = written(saved.getNetexId(), sp -> sp.getAccessibilityAssessment().getNetexId());
        Long updatedLimitationVersion = written(saved.getNetexId(), sp -> sp.getAccessibilityAssessment().getLimitations().getFirst().getVersion());

        assertThat(updatedAssessmentVersion).as("the server decides the version, not the client").isEqualTo(2L);
        assertThat(updatedAssessmentId).isEqualTo("NSR:AccessibilityAssessment:1");
        assertThat(updatedLimitationVersion).isEqualTo(1L);
    }

    /**
     * The case from issue #453. Two clients read version 1. The first write moves the stop place to
     * version 2. The second was built from what the first has already replaced, so applying it
     * would discard that work without telling anybody.
     */
    @Test
    public void updateFromAVersionThatHasMovedOnFails() throws InterruptedException {
        StopPlace saved = savedStopPlace("Original Name");

        StopPlaceJobDto firstJob = awaitJobCompletion(
                putXml(updateXml(saved.getNetexId(), 1, "First Edit"), StopPlaceJobDto.class).getBody().jobId());
        assertThat(firstJob.status()).isEqualTo(AsyncStopPlaceJobStatus.FINISHED);

        ResponseEntity<StopPlaceJobDto> second =
                putXml(updateXml(saved.getNetexId(), 1, "Second Edit"), StopPlaceJobDto.class);

        assertThat(second.getStatusCode())
                .as("staleness is found during processing, so the request is still accepted")
                .isEqualTo(HttpStatus.ACCEPTED);

        StopPlaceJobDto secondJob = awaitJobCompletion(second.getBody().jobId());

        assertThat(secondJob.status()).isEqualTo(AsyncStopPlaceJobStatus.FAILED);
        assertThat(secondJob.errorMessage())
                .as("the reason has to say the edit can be reapplied, and give the version to read")
                .contains("moved to version 2")
                .contains("Read it again");
        String survivingName = written(saved.getNetexId(), sp -> sp.getName().getValue());
        assertThat(survivingName).as("the first edit survives").isEqualTo("First Edit");
    }

    @Test
    public void updateWithoutAVersionFails() throws InterruptedException {
        StopPlace saved = savedStopPlace("Original Name");

        String xml = """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace id="%s">
                    <Name>No Version</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                </StopPlace>
            </stopPlaces>
            """.formatted(saved.getNetexId());

        StopPlaceJobDto job = awaitJobCompletion(putXml(xml, StopPlaceJobDto.class).getBody().jobId());

        assertThat(job.status()).isEqualTo(AsyncStopPlaceJobStatus.FAILED);
        assertThat(job.errorMessage()).contains("must carry the version attribute");
        String unchangedName = written(saved.getNetexId(), sp -> sp.getName().getValue());
        assertThat(unchangedName).isEqualTo("Original Name");
    }

    /**
     * version="any" is legal NeTEx, and is refused deliberately rather than as a side effect of
     * parsing. "any" is precisely the assertion a precondition cannot accept: it says the caller
     * does not care which version it edited.
     */
    @Test
    public void updateWithANonNumericVersionFails() throws InterruptedException {
        StopPlace saved = savedStopPlace("Original Name");

        String xml = """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace id="%s" version="any">
                    <Name>Any Version</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                </StopPlace>
            </stopPlaces>
            """.formatted(saved.getNetexId());

        StopPlaceJobDto job = awaitJobCompletion(putXml(xml, StopPlaceJobDto.class).getBody().jobId());

        assertThat(job.status()).isEqualTo(AsyncStopPlaceJobStatus.FAILED);
        assertThat(job.errorMessage())
                .as("the message reaches the caller verbatim, so it is part of the contract")
                .contains("version attribute must be a number")
                .contains("'any'");
        String unchangedName = written(saved.getNetexId(), sp -> sp.getName().getValue());
        assertThat(unchangedName).isEqualTo("Original Name");
    }

    @Test
    public void updateFromTheCurrentVersionSucceeds() throws InterruptedException {
        StopPlace saved = savedStopPlace("Original Name");

        StopPlaceJobDto job = awaitJobCompletion(
                putXml(updateXml(saved.getNetexId(), 1, "Edited"), StopPlaceJobDto.class).getBody().jobId());

        assertThat(job.status()).isEqualTo(AsyncStopPlaceJobStatus.FINISHED);
        String editedName = written(saved.getNetexId(), sp -> sp.getName().getValue());
        Long newVersion = written(saved.getNetexId(), StopPlace::getVersion);
        assertThat(editedName).isEqualTo("Edited");
        assertThat(newVersion).as("the server still decides the next version").isEqualTo(2L);
    }

    private StopPlace savedStopPlace(String name) {
        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString(name));
        stopPlace.setVersion(1L);
        stopPlace.setStopPlaceType(StopTypeEnumeration.BUS_STATION);
        return stopPlaceRepository.save(stopPlace);
    }

    private static String updateXml(String netexId, long version, String name) {
        return """
            <stopPlaces xmlns="http://www.netex.org.uk/netex">
                <StopPlace id="%s" version="%d">
                    <Name>%s</Name>
                    <StopPlaceType>busStation</StopPlaceType>
                </StopPlace>
            </stopPlaces>
            """.formatted(netexId, version, name);
    }

    @Autowired
    private PlatformTransactionManager transactionManager;

    /**
     * Reads what was actually written. These assertions used to go through the write API's own
     * GET, which no longer exists: reading is served by the read API. Asserting on the stored
     * entity is closer to what the tests mean anyway, since the question is whether the register
     * kept what it should, not how it renders. The transaction is for the lazy associations.
     */
    private <T> T written(String netexId, java.util.function.Function<StopPlace, T> extract) {
        return new TransactionTemplate(transactionManager).execute(status ->
                extract.apply(stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(netexId)));
    }

    private StopPlaceJobDto awaitJobCompletion(Long jobId)
        throws InterruptedException {
        String jobUrl = WRITE_ENDPOINT + "/jobs/" + jobId;
        for (int i = 0; i < 20; i++) {
            ResponseEntity<StopPlaceJobDto> jobResponse =
                restTemplate.getForEntity(jobUrl, StopPlaceJobDto.class);
            StopPlaceJobDto job = jobResponse.getBody();
            if (
                job != null &&
                job.status() != AsyncStopPlaceJobStatus.PROCESSING
            ) {
                return job;
            }
            Thread.sleep(200);
        }
        throw new AssertionError(
            "Job did not complete within the expected time"
        );
    }

    private <T> ResponseEntity<T> postXml(String xml, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> request = new HttpEntity<>(xml, headers);
        return restTemplate.postForEntity(
            WRITE_ENDPOINT,
            request,
            responseType
        );
    }

    private <T> ResponseEntity<T> putXml(String xml, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<String> request = new HttpEntity<>(xml, headers);
        return restTemplate.exchange(
            WRITE_ENDPOINT,
            HttpMethod.PUT,
            request,
            responseType
        );
    }
}
