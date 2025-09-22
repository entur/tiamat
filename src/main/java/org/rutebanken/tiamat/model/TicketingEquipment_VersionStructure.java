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

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import org.apache.commons.lang3.BooleanUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@MappedSuperclass
public class TicketingEquipment_VersionStructure
        extends InstalledEquipment_VersionStructure {

    protected Boolean ticketMachines;
    protected BigInteger numberOfMachines;
    protected Boolean ticketOffice;
    protected Boolean audioInterfaceAvailable;
    protected Boolean tactileInterfaceAvailable;

    @Transient
    protected List<VehicleModeEnumeration> vehicleModes;

    @Transient
    protected BigDecimal heightOfMachineInterface;

    @Transient
    protected List<TicketingFacilityEnumeration> ticketingFacilityList;

    @Transient
    protected Boolean ticketCounter;

    @Transient
    protected BigInteger numberOfTills;

    @Transient
    protected QueueManagementEnumeration queueManagement;

    @Transient
    protected List<PaymentMethodEnumeration> paymentMethods;

    @Transient
    protected List<TicketTypeEnumeration> ticketTypesAvailable;

    @Transient
    protected List<TicketingFacilityEnumeration> scopeOfTicketsAvailable;

    @Transient
    protected Boolean lowCounterAccess;

    @Transient
    protected BigDecimal heightOfLowCounter;

    @Transient
    protected Boolean inductionLoops;

    public List<VehicleModeEnumeration> getVehicleModes() {
        if (vehicleModes == null) {
            vehicleModes = new ArrayList<>();
        }
        return this.vehicleModes;
    }

    public Boolean isTicketMachines() {
        if(ticketMachines == null) {
            if (numberOfMachines != null) {
                return numberOfMachines.intValue() > 0;
            }
            return false;
        }
        return ticketMachines;
    }

    public void setTicketMachines(Boolean value) {
        this.ticketMachines = value;
    }

    public BigInteger getNumberOfMachines() {
        return numberOfMachines;
    }

    public void setNumberOfMachines(BigInteger value) {
        this.numberOfMachines = value;
    }

    public BigDecimal getHeightOfMachineInterface() {
        return heightOfMachineInterface;
    }

    public void setHeightOfMachineInterface(BigDecimal value) {
        this.heightOfMachineInterface = value;
    }

    public List<TicketingFacilityEnumeration> getTicketingFacilityList() {
        if (ticketingFacilityList == null) {
            ticketingFacilityList = new ArrayList<>();
        }
        return this.ticketingFacilityList;
    }

    public Boolean isTicketOffice() {
        return BooleanUtils.isTrue(ticketOffice);
    }

    public void setTicketOffice(Boolean value) {
        this.ticketOffice = value;
    }

    public Boolean isTicketCounter() {
        return ticketCounter;
    }

    public void setTicketCounter(Boolean value) {
        this.ticketCounter = value;
    }

    public BigInteger getNumberOfTills() {
        return numberOfTills;
    }

    public void setNumberOfTills(BigInteger value) {
        this.numberOfTills = value;
    }

    public QueueManagementEnumeration getQueueManagement() {
        return queueManagement;
    }

    public void setQueueManagement(QueueManagementEnumeration value) {
        this.queueManagement = value;
    }

    public List<PaymentMethodEnumeration> getPaymentMethods() {
        if (paymentMethods == null) {
            paymentMethods = new ArrayList<>();
        }
        return this.paymentMethods;
    }

    public List<TicketTypeEnumeration> getTicketTypesAvailable() {
        if (ticketTypesAvailable == null) {
            ticketTypesAvailable = new ArrayList<>();
        }
        return this.ticketTypesAvailable;
    }

    public List<TicketingFacilityEnumeration> getScopeOfTicketsAvailable() {
        if (scopeOfTicketsAvailable == null) {
            scopeOfTicketsAvailable = new ArrayList<>();
        }
        return this.scopeOfTicketsAvailable;
    }

    public Boolean isLowCounterAccess() {
        return lowCounterAccess;
    }

    public void setLowCounterAccess(Boolean value) {
        this.lowCounterAccess = value;
    }

    public BigDecimal getHeightOfLowCounter() {
        return heightOfLowCounter;
    }

    public void setHeightOfLowCounter(BigDecimal value) {
        this.heightOfLowCounter = value;
    }

    public Boolean isInductionLoops() {
        return inductionLoops;
    }

    public void setInductionLoops(Boolean value) {
        this.inductionLoops = value;
    }

    public Boolean isAudioInterfaceAvailable() {
        return audioInterfaceAvailable;
    }

    public void setAudioInterfaceAvailable(Boolean value) {
        this.audioInterfaceAvailable = value;
    }

    public Boolean isTactileInterfaceAvailable() {
        return tactileInterfaceAvailable;
    }

    public void setTactileInterfaceAvailable(Boolean value) {
        this.tactileInterfaceAvailable = value;
    }
}
