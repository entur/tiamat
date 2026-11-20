package org.rutebanken.tiamat.ext.fintraffic.api;

/**
 * Example response bodies for the OpenAPI description of the Read API.
 */
final class ReadApiOpenApiExamples {

    /**
     * A NeTEx Nordic PublicationDelivery with one stop place, its first quay, the stop assignment,
     * the municipality and one parking.
     */
    static final String PUBLICATION_DELIVERY = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <PublicationDelivery xmlns="http://www.netex.org.uk/netex" xmlns:ns2="http://www.opengis.net/gml/3.2" xmlns:ns3="http://www.siri.org.uk/siri" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="1.15:NO-NeTEx-stops:1.5" xsi:schemaLocation="">
                <PublicationTimestamp>2026-11-20T12:14:40.191895367</PublicationTimestamp>
                <ParticipantRef>FSR</ParticipantRef>
                <dataObjects>
                    <ServiceFrame modification="new" version="1" id="FSR:ServiceFrame:1795169680">
                        <scheduledStopPoints>
                            <ScheduledStopPoint version="2" id="FSR:ScheduledStopPoint:Q766">
                              <ValidBetween>
                                <FromDate>2024-12-31T22:00:00</FromDate>
                              </ValidBetween>
                              <Name>Pohjois-Haaga</Name>
                            </ScheduledStopPoint>
                        </scheduledStopPoints>
                        <stopAssignments>
                            <PassengerStopAssignment order="0" version="2" id="FSR:PassengerStopAssignment:PQ766">
                              <ValidBetween>
                                <FromDate>2024-12-31T22:00:00</FromDate>
                              </ValidBetween>
                              <ScheduledStopPointRef ref="FSR:ScheduledStopPoint:Q766" versionRef="2"/>
                              <QuayRef ref="FSR:Quay:766" version="2"/>
                            </PassengerStopAssignment>
                        </stopAssignments>
                    </ServiceFrame>
                    <SiteFrame modification="new" version="1" id="FSR:SiteFrame:1795169680">
                        <FrameDefaults>
                            <DefaultLocale>
                                <TimeZone>Europe/Helsinki</TimeZone>
                                <DefaultLanguage>eng</DefaultLanguage>
                            </DefaultLocale>
                        </FrameDefaults>
                        <topographicPlaces>
                            <TopographicPlace created="2026-04-29T06:26:17.919187" modification="new" version="1" id="FSR:TopographicPlace:91">
                              <ValidBetween>
                                <FromDate>2026-04-29T06:26:17.919187</FromDate>
                              </ValidBetween>
                              <Description lang="fi">Helsinki</Description>
                              <PrivateCode type="natcode">091</PrivateCode>
                              <Descriptor>
                                <Name lang="fi">Helsinki</Name>
                              </Descriptor>
                              <TopographicPlaceType>municipality</TopographicPlaceType>
                            </TopographicPlace>
                        </topographicPlaces>
                        <stopPlaces>
                            <StopPlace created="2026-04-13T06:05:54.067463" changed="2026-04-13T06:05:54.067463" modification="new" version="2" id="FSR:StopPlace:757">
                              <ValidBetween>
                                <FromDate>2024-12-31T22:00:00</FromDate>
                              </ValidBetween>
                              <keyList>
                                <KeyValue>
                                  <Key>ersad.PRMTSICompliantStation</Key>
                                  <Value>No</Value>
                                </KeyValue>
                                <KeyValue>
                                  <Key>uicCode</Key>
                                  <Value>1000657</Value>
                                </KeyValue>
                                <KeyValue>
                                  <Key>ersad.AccessibleStation</Key>
                                  <Value>Yes</Value>
                                </KeyValue>
                                <KeyValue>
                                  <Key>ersad.FacilityList.PassengerInformationEquipment.OpeningHours</Key>
                                  <Value>0-0</Value>
                                </KeyValue>
                                <KeyValue>
                                  <Key>ersad.equipmentPlaces.placeEquipments.TicketingEquipment.StepFreeAccess</Key>
                                  <Value>unknown</Value>
                                </KeyValue>
                                <KeyValue>
                                  <Key>ersad.FacilityList.MobilityFacilityList.tactileGuidingStrips</Key>
                                  <Value>false</Value>
                                </KeyValue>
                                <KeyValue>
                                  <Key>ersad.FacilityList.PassengerInformationEquipment.MobileFacilityList.audioForHearingImpaired</Key>
                                  <Value>false</Value>
                                </KeyValue>
                                <KeyValue>
                                  <Key>ersad.ReferencePoint</Key>
                                  <Value>Entrance</Value>
                                </KeyValue>
                                <KeyValue>
                                  <Key>ersad.equipmentPlaces.placeEquipments.TicketingEquipment.OpeningHours</Key>
                                  <Value>False</Value>
                                </KeyValue>
                                <KeyValue>
                                  <Key>ersad.FacilityList.MobilityFacilityList.StairTactiles</Key>
                                  <Value>false</Value>
                                </KeyValue>
                                <KeyValue>
                                  <Key>ersad.SanitaryEquipment.StepFreeAccess</Key>
                                  <Value>unknown</Value>
                                </KeyValue>
                                <KeyValue>
                                  <Key>ersad.FacilityList.PassengerInformationEquipment.MobileFacilityList.suitableForWheelchairs</Key>
                                  <Value>false</Value>
                                </KeyValue>
                                <KeyValue>
                                  <Key>ersad.StepFreeAccess</Key>
                                  <Value>unknown</Value>
                                </KeyValue>
                                <KeyValue>
                                  <Key>ersad.equipmentPlaces.AssistanceService.AlternateTransport</Key>
                                  <Value>No</Value>
                                </KeyValue>
                                <KeyValue>
                                  <Key>IS_PARENT_STOP_PLACE</Key>
                                  <Value>false</Value>
                                </KeyValue>
                                <KeyValue>
                                  <Key>peti_numeric_id</Key>
                                  <Value>1000757</Value>
                                </KeyValue>
                              </keyList>
                              <Name>Pohjois-Haaga</Name>
                              <Description></Description>
                              <Centroid>
                                <Location>
                                  <Longitude>24.883319</Longitude>
                                  <Latitude>60.230156</Latitude>
                                </Location>
                              </Centroid>
                              <Url>https://www.vr.fi/rautatieasemat-ja-reitit/pohjois-haaga</Url>
                              <PostalAddress modification="new" version="1" id="FSR:PostalAddress:152">
                                <AddressLine1>Kuparitie 3</AddressLine1>
                                <Town>Helsinki</Town>
                              </PostalAddress>
                              <AccessibilityAssessment modification="new" version="2" id="FSR:AccessibilityAssessment:760">
                                <MobilityImpairedAccess>unknown</MobilityImpairedAccess>
                                <limitations>
                                  <AccessibilityLimitation modification="new" version="1" id="FSR:AccessibilityLimitation:188">
                                    <WheelchairAccess>true</WheelchairAccess>
                                    <StepFreeAccess>true</StepFreeAccess>
                                    <EscalatorFreeAccess>unknown</EscalatorFreeAccess>
                                    <LiftFreeAccess>true</LiftFreeAccess>
                                    <AudibleSignalsAvailable>true</AudibleSignalsAvailable>
                                    <VisualSignsAvailable>true</VisualSignsAvailable>
                                  </AccessibilityLimitation>
                                </limitations>
                              </AccessibilityAssessment>
                              <facilities>
                                <SiteFacilitySet modification="new" version="0" id="FSR:SiteFacilitySet:455">
                                  <MobilityFacilityList>unknown</MobilityFacilityList>
                                  <PassengerInformationFacilityList>passengerInformationDisplay</PassengerInformationFacilityList>
                                </SiteFacilitySet>
                              </facilities>
                              <TopographicPlaceRef ref="FSR:TopographicPlace:91" created="2026-11-06T15:19:10.882860902" version="1"/>
                              <placeEquipments>
                                <TicketingEquipment modification="new" version="1" id="FSR:TicketingEquipment:455">
                                  <VehicleModes></VehicleModes>
                                  <TicketMachines>true</TicketMachines>
                                  <TicketingFacilityList></TicketingFacilityList>
                                  <TicketOffice>false</TicketOffice>
                                  <PaymentMethods></PaymentMethods>
                                  <TicketTypesAvailable></TicketTypesAvailable>
                                  <ScopeOfTicketsAvailable></ScopeOfTicketsAvailable>
                                  <LowCounterAccess>false</LowCounterAccess>
                                  <InductionLoops>false</InductionLoops>
                                  <TactileInterfaceAvailable>false</TactileInterfaceAvailable>
                                  <AudioInterfaceAvailable>false</AudioInterfaceAvailable>
                                  <WheelchairSuitable>false</WheelchairSuitable>
                                </TicketingEquipment>
                                <SanitaryEquipment modification="new" version="1" id="FSR:SanitaryEquipment:168">
                                  <SanitaryFacilityList></SanitaryFacilityList>
                                  <PaymentMethods></PaymentMethods>
                                </SanitaryEquipment>
                              </placeEquipments>
                              <localServices>
                                <AssistanceService modification="new" version="1" id="FSR:AssistanceService:152">
                                  <AssistanceFacilityList>none</AssistanceFacilityList>
                                  <AssistanceAvailability>none</AssistanceAvailability>
                                </AssistanceService>
                              </localServices>
                              <TransportMode>rail</TransportMode>
                              <StopPlaceType>railStation</StopPlaceType>
                              <Weighting>interchangeAllowed</Weighting>
                              <quays>
                                <Quay modification="new" version="2" id="FSR:Quay:766">
                                  <keyList>
                                    <KeyValue>
                                      <Key>peti_numeric_id</Key>
                                      <Value>5000766</Value>
                                    </KeyValue>
                                  </keyList>
                                  <Centroid>
                                    <Location>
                                      <Longitude>24.883233</Longitude>
                                      <Latitude>60.230102</Latitude>
                                    </Location>
                                  </Centroid>
                                  <facilities>
                                    <SiteFacilitySet modification="new" version="0" id="FSR:SiteFacilitySet:459">
                                      <MobilityFacilityList>unknown</MobilityFacilityList>
                                    </SiteFacilitySet>
                                  </facilities>
                                  <placeEquipments>
                                    <TicketingEquipment modification="new" version="1" id="FSR:TicketingEquipment:459">
                                      <VehicleModes></VehicleModes>
                                      <TicketMachines>true</TicketMachines>
                                      <TicketingFacilityList></TicketingFacilityList>
                                      <TicketOffice>false</TicketOffice>
                                      <PaymentMethods></PaymentMethods>
                                      <TicketTypesAvailable></TicketTypesAvailable>
                                      <ScopeOfTicketsAvailable></ScopeOfTicketsAvailable>
                                      <LowCounterAccess>false</LowCounterAccess>
                                      <InductionLoops>false</InductionLoops>
                                      <TactileInterfaceAvailable>false</TactileInterfaceAvailable>
                                      <AudioInterfaceAvailable>false</AudioInterfaceAvailable>
                                      <WheelchairSuitable>true</WheelchairSuitable>
                                    </TicketingEquipment>
                                  </placeEquipments>
                                  <PublicCode>1</PublicCode>
                                </Quay>
                              </quays>
                            </StopPlace>
                        </stopPlaces>
                        <parkings>
                            <Parking created="2026-11-06T14:51:07.571454" changed="2026-11-01T01:00:38.924" modification="new" version="2" id="FSR:Parking:100041">
                              <validityConditions>
                                <AvailabilityCondition version="1" id="FSR:Parking:100041:AvailabilityCondition:1">
                                  <IsAvailable>true</IsAvailable>
                                  <dayTypes>
                                    <DayTypeRef ref="FSR:DayType:BusinessDay"/>
                                  </dayTypes>
                                  <timebands>
                                    <Timeband version="1" id="FSR:Parking:100041:Timeband:1">
                                      <StartTime>00:00:00</StartTime>
                                      <EndTime>00:00:00</EndTime>
                                      <DayOffset>1</DayOffset>
                                    </Timeband>
                                  </timebands>
                                </AvailabilityCondition>
                                <AvailabilityCondition version="1" id="FSR:Parking:100041:AvailabilityCondition:2">
                                  <IsAvailable>true</IsAvailable>
                                  <dayTypes>
                                    <DayTypeRef ref="FSR:DayType:Sunday"/>
                                  </dayTypes>
                                  <timebands>
                                    <Timeband version="1" id="FSR:Parking:100041:Timeband:2">
                                      <StartTime>00:00:00</StartTime>
                                      <EndTime>00:00:00</EndTime>
                                      <DayOffset>1</DayOffset>
                                    </Timeband>
                                  </timebands>
                                </AvailabilityCondition>
                                <AvailabilityCondition version="1" id="FSR:Parking:100041:AvailabilityCondition:3">
                                  <IsAvailable>true</IsAvailable>
                                  <dayTypes>
                                    <DayTypeRef ref="FSR:DayType:Saturday"/>
                                  </dayTypes>
                                  <timebands>
                                    <Timeband version="1" id="FSR:Parking:100041:Timeband:3">
                                      <StartTime>00:00:00</StartTime>
                                      <EndTime>00:00:00</EndTime>
                                      <DayOffset>1</DayOffset>
                                    </Timeband>
                                  </timebands>
                                </AvailabilityCondition>
                              </validityConditions>
                              <keyList>
                                <KeyValue>
                                  <Key>imported-id</Key>
                                  <Value>LIIPI:Facility:41</Value>
                                </KeyValue>
                              </keyList>
                              <Name lang="fi">Kuparitie</Name>
                              <Centroid>
                                <Location>
                                  <Longitude>24.884044</Longitude>
                                  <Latitude>60.230902</Latitude>
                                </Location>
                              </Centroid>
                              <alternativeNames>
                                <AlternativeName modification="new" version="1" id="FSR:AlternativeName:11427">
                                  <Lang>sv</Lang>
                                  <NameType>translation</NameType>
                                  <Name lang="sv">Kopparvägen</Name>
                                </AlternativeName>
                                <AlternativeName modification="new" version="1" id="FSR:AlternativeName:11429">
                                  <Lang>en</Lang>
                                  <NameType>translation</NameType>
                                  <Name lang="en">Kuparitie</Name>
                                </AlternativeName>
                              </alternativeNames>
                              <Lighting>wellLit</Lighting>
                              <OperatorRef ref="FSR:Operator:1109447-9"/>
                              <ParentSiteRef ref="FSR:StopPlace:757"/>
                              <ParkingType>parkAndRide</ParkingType>
                              <ParkingVehicleTypes>car</ParkingVehicleTypes>
                              <TotalCapacity>83</TotalCapacity>
                              <ParkingPaymentProcess>free</ParkingPaymentProcess>
                              <parkingProperties>
                                <ParkingProperties version="2" id="FSR:ParkingProperties:100041">
                                  <spaces>
                                    <ParkingCapacity version="2" id="FSR:ParkingCapacity:1000411">
                                      <ParkingUserType>allUsers</ParkingUserType>
                                      <ParkingVehicleType>car</ParkingVehicleType>
                                      <NumberOfSpaces>83</NumberOfSpaces>
                                    </ParkingCapacity>
                                  </spaces>
                                </ParkingProperties>
                              </parkingProperties>
                            </Parking>
                        </parkings>
                    </SiteFrame>
                </dataObjects>
            </PublicationDelivery>
            """;

    private ReadApiOpenApiExamples() {
    }
}
