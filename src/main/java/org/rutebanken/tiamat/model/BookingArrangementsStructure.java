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

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;


public class BookingArrangementsStructure {

    protected List<BookingMethodEnumeration> bookingMethods;
    protected BookingAccessEnumeration bookingAccess;
    protected PurchaseWhenEnumeration bookWhen;
    protected List<PurchaseMomentEnumeration> buyWhen;
    protected XMLGregorianCalendar latestBookingTime;
    protected Duration minimumBookingPeriod;
    protected String bookingUrl;
    protected MultilingualStringEntity bookingNote;

    public List<BookingMethodEnumeration> getBookingMethods() {
        if (bookingMethods == null) {
            bookingMethods = new ArrayList<>();
        }
        return this.bookingMethods;
    }

    public BookingAccessEnumeration getBookingAccess() {
        return bookingAccess;
    }

    public void setBookingAccess(BookingAccessEnumeration value) {
        this.bookingAccess = value;
    }

    public PurchaseWhenEnumeration getBookWhen() {
        return bookWhen;
    }

    public void setBookWhen(PurchaseWhenEnumeration value) {
        this.bookWhen = value;
    }

    public List<PurchaseMomentEnumeration> getBuyWhen() {
        if (buyWhen == null) {
            buyWhen = new ArrayList<>();
        }
        return this.buyWhen;
    }

    public XMLGregorianCalendar getLatestBookingTime() {
        return latestBookingTime;
    }

    public void setLatestBookingTime(XMLGregorianCalendar value) {
        this.latestBookingTime = value;
    }

    public Duration getMinimumBookingPeriod() {
        return minimumBookingPeriod;
    }

    public void setMinimumBookingPeriod(Duration value) {
        this.minimumBookingPeriod = value;
    }

    public String getBookingUrl() {
        return bookingUrl;
    }

    public void setBookingUrl(String value) {
        this.bookingUrl = value;
    }

    public MultilingualStringEntity getBookingNote() {
        return bookingNote;
    }

    public void setBookingNote(MultilingualStringEntity value) {
        this.bookingNote = value;
    }

}
