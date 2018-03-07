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

import com.google.common.base.MoreObjects;

import javax.persistence.Embeddable;
import java.time.Instant;

@Embeddable
public class ValidBetween {

    private Instant fromDate;

    private Instant toDate;

    public ValidBetween(Instant fromDate, Instant toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public ValidBetween(Instant fromDate) {
        this.fromDate = fromDate;
    }

    public ValidBetween() {
    }

    public Instant getFromDate() {
        return fromDate;
    }

    public void setFromDate(Instant fromDate) {
        this.fromDate = fromDate;
    }

    public Instant getToDate() {
        return toDate;
    }

    public void setToDate(Instant toDate) {
        this.toDate = toDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValidBetween that = (ValidBetween) o;

        if (fromDate != null ? !fromDate.equals(that.fromDate) : that.fromDate != null) return false;
        if (toDate != null ? !toDate.equals(that.toDate) : that.toDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = fromDate != null ? fromDate.hashCode() : 0;
        result = 31 * result + (toDate != null ? toDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("from", fromDate)
                .add("to", toDate)
                .toString();
    }
}
