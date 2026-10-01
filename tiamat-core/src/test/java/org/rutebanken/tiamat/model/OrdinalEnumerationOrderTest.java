/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.model;

import jakarta.persistence.Enumerated;
import org.junit.Test;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJob;
import org.rutebanken.tiamat.model.job.AsyncStopPlaceJobStatus;
import org.rutebanken.tiamat.model.job.ExportJob;
import org.rutebanken.tiamat.model.job.JobStatus;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pins the declaration order of the enumerations that JPA stores as an ordinal.
 * <p>
 * The column holds the position of the constant, so the position is part of the stored data.
 * A constant that moves changes the meaning of every row that already holds the field, and
 * nothing else reports it. The assertions name the position the database already contains.
 * A new constant at the end keeps them true, so this test permits an addition and rejects a
 * move, an insertion and a deletion.
 * <p>
 * The last test holds the condition the others depend on. An {@link Enumerated} annotation on
 * one of these fields changes the column contents from a position to a name.
 */
public class OrdinalEnumerationOrderTest {

    @Test
    public void parkingLayoutEnumerationKeepsItsStoredOrder() {
        assertThat(ParkingLayoutEnumeration.COVERED.ordinal()).isEqualTo(0);
        assertThat(ParkingLayoutEnumeration.OPEN_SPACE.ordinal()).isEqualTo(1);
        assertThat(ParkingLayoutEnumeration.MULTISTOREY.ordinal()).isEqualTo(2);
        assertThat(ParkingLayoutEnumeration.UNDERGROUND.ordinal()).isEqualTo(3);
        assertThat(ParkingLayoutEnumeration.ROADSIDE.ordinal()).isEqualTo(4);
        assertThat(ParkingLayoutEnumeration.UNDEFINED.ordinal()).isEqualTo(5);
        assertThat(ParkingLayoutEnumeration.OTHER.ordinal()).isEqualTo(6);
        assertThat(ParkingLayoutEnumeration.CYCLE_HIRE.ordinal()).isEqualTo(7);
    }

    @Test
    public void parkingReservationEnumerationKeepsItsStoredOrder() {
        assertThat(ParkingReservationEnumeration.RESERVATION_REQUIRED.ordinal()).isEqualTo(0);
        assertThat(ParkingReservationEnumeration.RESERVATION_ALLOWED.ordinal()).isEqualTo(1);
        assertThat(ParkingReservationEnumeration.NO_RESERVATIONS.ordinal()).isEqualTo(2);
        assertThat(ParkingReservationEnumeration.REGISTRATION_REQUIRED.ordinal()).isEqualTo(3);
        assertThat(ParkingReservationEnumeration.OTHER.ordinal()).isEqualTo(4);
    }

    @Test
    public void cycleStorageEnumerationKeepsItsStoredOrder() {
        assertThat(CycleStorageEnumeration.RACKS.ordinal()).isEqualTo(0);
        assertThat(CycleStorageEnumeration.BARS.ordinal()).isEqualTo(1);
        assertThat(CycleStorageEnumeration.RAILINGS.ordinal()).isEqualTo(2);
        assertThat(CycleStorageEnumeration.CYCLE_SCHEME.ordinal()).isEqualTo(3);
        assertThat(CycleStorageEnumeration.OTHER.ordinal()).isEqualTo(4);
    }

    @Test
    public void genderLimitationEnumerationKeepsItsStoredOrder() {
        assertThat(GenderLimitationEnumeration.BOTH.ordinal()).isEqualTo(0);
        assertThat(GenderLimitationEnumeration.FEMALE_ONLY.ordinal()).isEqualTo(1);
        assertThat(GenderLimitationEnumeration.MALE_ONLY.ordinal()).isEqualTo(2);
        assertThat(GenderLimitationEnumeration.SAME_SEX_ONLY.ordinal()).isEqualTo(3);
    }

    @Test
    public void coveredEnumerationKeepsItsStoredOrder() {
        assertThat(CoveredEnumeration.INDOORS.ordinal()).isEqualTo(0);
        assertThat(CoveredEnumeration.OUTDOORS.ordinal()).isEqualTo(1);
        assertThat(CoveredEnumeration.COVERED.ordinal()).isEqualTo(2);
        assertThat(CoveredEnumeration.MIXED.ordinal()).isEqualTo(3);
        assertThat(CoveredEnumeration.UNKNOWN.ordinal()).isEqualTo(4);
    }

    @Test
    public void jobStatusKeepsItsStoredOrder() {
        assertThat(JobStatus.PROCESSING.ordinal()).isEqualTo(0);
        assertThat(JobStatus.FINISHED.ordinal()).isEqualTo(1);
        assertThat(JobStatus.FAILED.ordinal()).isEqualTo(2);
    }

    @Test
    public void asyncStopPlaceJobStatusKeepsItsStoredOrder() {
        assertThat(AsyncStopPlaceJobStatus.PROCESSING.ordinal()).isEqualTo(0);
        assertThat(AsyncStopPlaceJobStatus.FINISHED.ordinal()).isEqualTo(1);
        assertThat(AsyncStopPlaceJobStatus.FAILED.ordinal()).isEqualTo(2);
        assertThat(AsyncStopPlaceJobStatus.IN_PROGRESS.ordinal()).isEqualTo(3);
        assertThat(AsyncStopPlaceJobStatus.TIMED_OUT.ordinal()).isEqualTo(4);
    }

    @Test
    public void fieldsThatStoreTheseEnumerationsCarryNoEnumeratedAnnotation() throws Exception {
        assertNoEnumeratedAnnotation(Parking.class, "parkingLayout");
        assertNoEnumeratedAnnotation(Parking.class, "parkingReservation");
        assertNoEnumeratedAnnotation(CycleStorageEquipment_VersionStructure.class, "cycleStorageType");
        assertNoEnumeratedAnnotation(SanitaryEquipment_VersionStructure.class, "gender");
        assertNoEnumeratedAnnotation(SiteElement.class, "covered");
        assertNoEnumeratedAnnotation(ExportJob.class, "status");
        assertNoEnumeratedAnnotation(AsyncStopPlaceJob.class, "status");
    }

    private void assertNoEnumeratedAnnotation(Class<?> owner, String fieldName) throws NoSuchFieldException {
        Field field = owner.getDeclaredField(fieldName);
        assertThat(field.getAnnotation(Enumerated.class))
                .as("%s.%s stores an ordinal, so the column holds an integer", owner.getSimpleName(), fieldName)
                .isNull();
    }
}
