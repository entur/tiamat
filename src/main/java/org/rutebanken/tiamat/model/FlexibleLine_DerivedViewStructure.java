

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;


public class FlexibleLine_DerivedViewStructure
    extends Line_DerivedViewStructure
{

    protected FlexibleLineTypeEnumeration flexibleLineType;
    protected ContactStructure bookingContact;
    protected List<BookingMethodEnumeration> bookingMethods;
    protected BookingAccessEnumeration bookingAccess;
    protected PurchaseWhenEnumeration bookWhen;
    protected List<PurchaseMomentEnumeration> buyWhen;
    protected XMLGregorianCalendar latestBookingTime;
    protected Duration minimumBookingPeriod;
    protected String bookingUrl;
    protected MultilingualStringEntity bookingNote;

    public FlexibleLineTypeEnumeration getFlexibleLineType() {
        return flexibleLineType;
    }

    public void setFlexibleLineType(FlexibleLineTypeEnumeration value) {
        this.flexibleLineType = value;
    }

    public ContactStructure getBookingContact() {
        return bookingContact;
    }

    public void setBookingContact(ContactStructure value) {
        this.bookingContact = value;
    }

    public List<BookingMethodEnumeration> getBookingMethods() {
        if (bookingMethods == null) {
            bookingMethods = new ArrayList<BookingMethodEnumeration>();
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
            buyWhen = new ArrayList<PurchaseMomentEnumeration>();
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
