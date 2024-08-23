package org.rutebanken.tiamat.rest.graphql;

import org.junit.Before;
import org.junit.Test;
import org.rutebanken.helper.organisation.AuthorizationConstants;
import org.rutebanken.tiamat.auth.MockedRoleAssignmentExtractor;
import org.rutebanken.tiamat.auth.RoleAssignmentListBuilder;
import org.rutebanken.tiamat.model.Contact;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Organisation;
import org.rutebanken.tiamat.model.OrganisationTypeEnumeration;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

public class GraphQLResourceOrganisationIntegrationTest extends AbstractGraphQLResourceIntegrationTest {

    @Autowired
    private MockedRoleAssignmentExtractor mockedRoleAssignmentExtractor;

    @Before
    public void setAuthWithOrganisationROle() {
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(
            RoleAssignmentListBuilder.builder()
                    .withAccessAllTypesForRole(AuthorizationConstants.ROLE_ORGANISATION_EDIT)
                    .build()
        );
    }

    private void setAuthWithoutOrganisationRole() {
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(
            RoleAssignmentListBuilder.builder()
                    .withAccessAllTypesForRole(AuthorizationConstants.ROLE_EDIT_STOPS)
                    .build()
        );
    }

    @Test
    public void testQueryOrganisationById() {
        Organisation organisation = createTestOrganisation();

        organisationRepository.save(organisation);

        String graphQlJsonQuery = """
            {
              organisation: organisation (id: "%s") {
                id
                privateCode
                companyNumber
                name
                organisationType
                legalName {
                  value
                  lang
                }
                contactDetails {
                  contactPerson
                  email
                  phone
                  fax
                  url
                  furtherDetails
                }
                privateContactDetails {
                  contactPerson
                  email
                  phone
                  fax
                  url
                  furtherDetails
                }
              }
            }""".formatted(organisation.getNetexId());

        Contact contactDetails = organisation.getContactDetails();
        Contact privateContactDetails = organisation.getPrivateContactDetails();

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.organisation", hasSize(1))
                .body("data.organisation[0].id", equalTo(organisation.getNetexId()))
                .body("data.organisation[0].privateCode", equalTo(organisation.getPrivateCode()))
                .body("data.organisation[0].companyNumber", equalTo(organisation.getCompanyNumber()))
                .body("data.organisation[0].name", equalTo(organisation.getName()))
                .body("data.organisation[0].organisationType", equalTo(organisation.getOrganisationType().value()))
                .body("data.organisation[0].legalName.value", equalTo(organisation.getLegalName().getValue()))
                .body("data.organisation[0].legalName.lang", equalTo(organisation.getLegalName().getLang()))
                .body("data.organisation[0].contactDetails.contactPerson", equalTo(contactDetails.getContactPerson()))
                .body("data.organisation[0].contactDetails.email", equalTo(contactDetails.getEmail()))
                .body("data.organisation[0].contactDetails.phone", equalTo(contactDetails.getPhone()))
                .body("data.organisation[0].contactDetails.fax", equalTo(contactDetails.getFax()))
                .body("data.organisation[0].contactDetails.url", equalTo(contactDetails.getUrl()))
                .body("data.organisation[0].contactDetails.furtherDetails", equalTo(contactDetails.getFurtherDetails()))
                .body("data.organisation[0].privateContactDetails.contactPerson", equalTo(privateContactDetails.getContactPerson()))
                .body("data.organisation[0].privateContactDetails.email", equalTo(privateContactDetails.getEmail()))
                .body("data.organisation[0].privateContactDetails.phone", equalTo(privateContactDetails.getPhone()))
                .body("data.organisation[0].privateContactDetails.fax", equalTo(privateContactDetails.getFax()))
                .body("data.organisation[0].privateContactDetails.url", equalTo(privateContactDetails.getUrl()))
                .body("data.organisation[0].privateContactDetails.furtherDetails", equalTo(privateContactDetails.getFurtherDetails()));
    }

    @Test
    public void testQueryAllOrganisations() {
        Organisation organisation = createTestOrganisation();

        organisationRepository.save(organisation);

        String graphQlJsonQuery = """
            {
              organisation {
                id
              }
            }""";

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.organisation", hasSize(1))
                .body("data.organisation[0].id", equalTo(organisation.getNetexId()));

    }

    @Test
    public void testMutateOrganisationCreateNew() {
         String graphQlJsonQuery = """
            mutation {
              organisation: mutateOrganisation (
                Organisation: {
                  privateCode: "PrivCode",
                  companyNumber: "112233",
                  name: "Test Organisation",
                  organisationType: other,
                  legalName: {
                    value: "Test Organisation Oy",
                    lang: "fi"
                  },
                  contactDetails: {
                    contactPerson: "Person Tester",
                    email: "noreply@example.com",
                    phone: "+358502345678",
                    fax: "+358509876543",
                    url: "www.another-example.com",
                    furtherDetails: "new details"
                  },
                  privateContactDetails: {
                    contactPerson: "John Doe",
                    email: null,
                    phone: "+358501122333"
                  }
                }
            ) {
                id
                version
                privateCode
                companyNumber
                name
                organisationType
                legalName {
                  value
                  lang
                }
                contactDetails {
                  contactPerson
                  email
                  phone
                  fax
                  url
                  furtherDetails
                }
                privateContactDetails {
                  contactPerson
                  email
                  phone
                  fax
                  url
                  furtherDetails
                }
              }
            }""";

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.organisation", hasSize(1))
                .body("data.organisation[0].id", notNullValue())
                .body("data.organisation[0].version", equalTo("1"))
                .body("data.organisation[0].privateCode", equalTo("PrivCode"))
                .body("data.organisation[0].companyNumber", equalTo("112233"))
                .body("data.organisation[0].name", equalTo("Test Organisation"))
                .body("data.organisation[0].organisationType", equalTo(OrganisationTypeEnumeration.OTHER.value()))
                .body("data.organisation[0].legalName.value", equalTo("Test Organisation Oy"))
                .body("data.organisation[0].legalName.lang", equalTo("fi"))
                .body("data.organisation[0].contactDetails.contactPerson", equalTo("Person Tester"))
                .body("data.organisation[0].contactDetails.email", equalTo("noreply@example.com"))
                .body("data.organisation[0].contactDetails.phone", equalTo("+358502345678"))
                .body("data.organisation[0].contactDetails.fax", equalTo("+358509876543"))
                .body("data.organisation[0].contactDetails.url", equalTo("www.another-example.com"))
                .body("data.organisation[0].contactDetails.furtherDetails", equalTo("new details"))
                .body("data.organisation[0].privateContactDetails.contactPerson", equalTo("John Doe"))
                .body("data.organisation[0].privateContactDetails.email", equalTo(null))
                .body("data.organisation[0].privateContactDetails.phone", equalTo("+358501122333"))
                .body("data.organisation[0].privateContactDetails.fax", equalTo(null))
                .body("data.organisation[0].privateContactDetails.url", equalTo(null))
                .body("data.organisation[0].privateContactDetails.furtherDetails", equalTo(null));
                ;
    }

    @Test
    public void testMutateOrganisationUpdateExisting() {
        Organisation organisation = createTestOrganisation();

        organisationRepository.save(organisation);

        String graphQlJsonQuery = """
            mutation {
              organisation: mutateOrganisation (
                Organisation: {
                  id: "%s",
                  privateCode: "new code",
                  companyNumber: "223344",
                  name: "New Organisation",
                  organisationType: servicedOrganisation,
                  legalName: {
                    value: "New Organisation Oy",
                    lang: "fi"
                  },
                  contactDetails: {
                    contactPerson: "Person Tester",
                    email: "noreply@example.com",
                    phone: "+358502345678",
                    fax: "+358509876543",
                    url: "www.another-example.com",
                    furtherDetails: "new details"
                  },
                  privateContactDetails: {
                    contactPerson: "John Doe",
                    email: null,
                    phone: "+358501122333"
                  }
                }
            ) {
                id
                version
                privateCode
                companyNumber
                name
                organisationType
                legalName {
                  value
                  lang
                }
                contactDetails {
                  contactPerson
                  email
                  phone
                  fax
                  url
                  furtherDetails
                }
                privateContactDetails {
                  contactPerson
                  email
                  phone
                  fax
                  url
                  furtherDetails
                }
              }
            }""".formatted(organisation.getNetexId());

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.organisation", hasSize(1))
                .body("data.organisation[0].id", equalTo(organisation.getNetexId()))
                .body("data.organisation[0].version", equalTo("1"))
                .body("data.organisation[0].privateCode", equalTo("new code"))
                .body("data.organisation[0].companyNumber", equalTo("223344"))
                .body("data.organisation[0].name", equalTo("New Organisation"))
                .body("data.organisation[0].organisationType", equalTo(OrganisationTypeEnumeration.SERVICED_ORGANISATION.value()))
                .body("data.organisation[0].legalName.value", equalTo("New Organisation Oy"))
                .body("data.organisation[0].legalName.lang", equalTo("fi"))
                .body("data.organisation[0].contactDetails.contactPerson", equalTo("Person Tester"))
                .body("data.organisation[0].contactDetails.email", equalTo("noreply@example.com"))
                .body("data.organisation[0].contactDetails.phone", equalTo("+358502345678"))
                .body("data.organisation[0].contactDetails.fax", equalTo("+358509876543"))
                .body("data.organisation[0].contactDetails.url", equalTo("www.another-example.com"))
                .body("data.organisation[0].contactDetails.furtherDetails", equalTo("new details"))
                .body("data.organisation[0].privateContactDetails.contactPerson", equalTo("John Doe"))
                .body("data.organisation[0].privateContactDetails.email", equalTo(null))
                .body("data.organisation[0].privateContactDetails.phone", equalTo("+358501122333"))
                .body("data.organisation[0].privateContactDetails.fax", equalTo(null))
                .body("data.organisation[0].privateContactDetails.url", equalTo(null))
                .body("data.organisation[0].privateContactDetails.furtherDetails", equalTo(null));
    }

    @Test
    public void testMutateOrganisationUpdateWithoutContactData() {
        Organisation organisation = createTestOrganisation();

        organisationRepository.save(organisation);
        Contact contactDetails = organisation.getContactDetails();
        Contact privateContactDetails = organisation.getPrivateContactDetails();

        String graphQlJsonQuery = """
            mutation {
              organisation: mutateOrganisation (
                Organisation: {
                  id: "%s",
                  name: "New Organisation"
                }
            ) {
                id
                version
                privateCode
                companyNumber
                name
                organisationType
                legalName {
                  value
                  lang
                }
                contactDetails {
                  contactPerson
                  email
                  phone
                  fax
                  url
                  furtherDetails
                }
                privateContactDetails {
                  contactPerson
                  email
                  phone
                  fax
                  url
                  furtherDetails
                }
              }
            }""".formatted(organisation.getNetexId());

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.organisation", hasSize(1))
                .body("data.organisation[0].id", equalTo(organisation.getNetexId()))
                .body("data.organisation[0].privateCode", equalTo(organisation.getPrivateCode()))
                .body("data.organisation[0].companyNumber", equalTo(organisation.getCompanyNumber()))
                .body("data.organisation[0].name", equalTo("New Organisation"))
                .body("data.organisation[0].organisationType", equalTo(organisation.getOrganisationType().value()))
                .body("data.organisation[0].legalName.value", equalTo(organisation.getLegalName().getValue()))
                .body("data.organisation[0].legalName.lang", equalTo(organisation.getLegalName().getLang()))
                .body("data.organisation[0].contactDetails.contactPerson", equalTo(contactDetails.getContactPerson()))
                .body("data.organisation[0].contactDetails.email", equalTo(contactDetails.getEmail()))
                .body("data.organisation[0].contactDetails.phone", equalTo(contactDetails.getPhone()))
                .body("data.organisation[0].contactDetails.fax", equalTo(contactDetails.getFax()))
                .body("data.organisation[0].contactDetails.url", equalTo(contactDetails.getUrl()))
                .body("data.organisation[0].contactDetails.furtherDetails", equalTo(contactDetails.getFurtherDetails()))
                .body("data.organisation[0].privateContactDetails.contactPerson", equalTo(privateContactDetails.getContactPerson()))
                .body("data.organisation[0].privateContactDetails.email", equalTo(privateContactDetails.getEmail()))
                .body("data.organisation[0].privateContactDetails.phone", equalTo(privateContactDetails.getPhone()))
                .body("data.organisation[0].privateContactDetails.fax", equalTo(privateContactDetails.getFax()))
                .body("data.organisation[0].privateContactDetails.url", equalTo(privateContactDetails.getUrl()))
                .body("data.organisation[0].privateContactDetails.furtherDetails", equalTo(privateContactDetails.getFurtherDetails()));
    }

    @Test
    public void testUpdateOrganisationAndQueryReturnsNewestVersion() {
        Organisation organisation = createTestOrganisation();

        organisationRepository.save(organisation);

        String udpateOrganisationQuery = """
            mutation {
              organisation: mutateOrganisation (
                Organisation: {
                  id: "%s",
                  name: "New Organisation Name",
                  privateContactDetails: {
                    contactPerson: "John Doe",
                    email: null,
                    phone: "+358501122333"
                  }
                }
            ) {
                id
                version
                name
                contactDetails {
                  id
                  version
                }
                privateContactDetails {
                  id
                  version
                  contactPerson
                }
              }
            }""".formatted(organisation.getNetexId());

        executeGraphqQLQueryOnly(udpateOrganisationQuery)
                .body("data.organisation", hasSize(1))
                .body("data.organisation[0].id", equalTo(organisation.getNetexId()))
                .body("data.organisation[0].version", equalTo("1"))
                .body("data.organisation[0].name", equalTo("New Organisation Name"))
                // Private contact details were modified, version incremented.
                .body("data.organisation[0].privateContactDetails.id", equalTo(organisation.getPrivateContactDetails().getNetexId()))
                .body("data.organisation[0].privateContactDetails.version", equalTo("1"))
                .body("data.organisation[0].privateContactDetails.contactPerson", equalTo("John Doe"))
                // Contact details were not modified, but still a new version is created.
                .body("data.organisation[0].contactDetails.id", equalTo(organisation.getContactDetails().getNetexId()))
                .body("data.organisation[0].contactDetails.version", equalTo("1"));

       String allOrganisationsQuery = """
            {
              organisation {
                id
                version
                contactDetails {
                  id
                  version
                }
                privateContactDetails {
                  id
                  version
                }
              }
            }""";

        executeGraphqQLQueryOnly(allOrganisationsQuery)
                .body("data.organisation", hasSize(1))
                .body("data.organisation[0].id", equalTo(organisation.getNetexId()))
                .body("data.organisation[0].version", equalTo("1"))
                .body("data.organisation[0].privateContactDetails.id", equalTo(organisation.getPrivateContactDetails().getNetexId()))
                .body("data.organisation[0].privateContactDetails.version", equalTo("1"))
                .body("data.organisation[0].contactDetails.id", equalTo(organisation.getContactDetails().getNetexId()))
                .body("data.organisation[0].contactDetails.version", equalTo("1"));
    }

    @Test
    public void testDeleteOrganisation() {
        Organisation fillerOrganisation1 = createTestOrganisation();
        fillerOrganisation1.setName("Filler organisation 1");
        Organisation fillerOrganisation2 = createTestOrganisation();
        fillerOrganisation2.setName("Filler organisation 2");
        Organisation organisation = createTestOrganisation();
        organisationRepository.save(fillerOrganisation1);
        organisationRepository.save(organisation);
        organisationRepository.save(fillerOrganisation2);

        String graphQlJsonQuery = """
            mutation {
              organisation: deleteOrganisation (
                organisationId: "%s"
              )
            }""".formatted(organisation.getNetexId());

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.organisation", equalTo(true));

        // Check that other organisations are still present
        executeGraphqQLQueryOnly("""
            {
              organisation {
                id
              }
            }""")
                .body("data.organisation", hasSize(2))
                .body("data.organisation[0].id", equalTo(fillerOrganisation1.getNetexId()))
                .body("data.organisation[1].id", equalTo(fillerOrganisation2.getNetexId()));
    }

    @Test
    public void testDeleteOrganisationWithoutAuthorization() {
        setAuthWithoutOrganisationRole();

        Organisation organisation = createTestOrganisation();
        organisationRepository.save(organisation);

        String graphQlJsonQuery = """
            mutation {
              organisation: deleteOrganisation (
                organisationId: "%s"
              )
            }""".formatted(organisation.getNetexId());

        executeGraphqQLQueryOnly(graphQlJsonQuery,403);
    }

    @Test
    public void testMutateOrganisationWithoutAuthorization() {
        setAuthWithoutOrganisationRole();

        Organisation organisation = createTestOrganisation();
        organisationRepository.save(organisation);

        String graphQlJsonQuery = """
            mutation {
              organisation: mutateOrganisation (
                Organisation: {
                  id: "%s",
                  name: "New Name"
                }
            ) {
                id
              }
            }""".formatted(organisation.getNetexId());

        executeGraphqQLQueryOnly(graphQlJsonQuery,403);
    }

    protected Organisation createTestOrganisation() {
        Organisation organisation = new Organisation();
        organisation.setPrivateCode("PrivCode");
        organisation.setCompanyNumber("112233");
        organisation.setName("Test Organisation");
        organisation.setOrganisationType(OrganisationTypeEnumeration.OTHER);
        organisation.setLegalName(new EmbeddableMultilingualString("Test Organisation Oy", "fi"));

        Contact contactDetails = new Contact();
        contactDetails.setContactPerson("Test Person");
        contactDetails.setEmail("null@example.com");
        contactDetails.setPhone("+358501234567");
        contactDetails.setFax("+358507654321");
        contactDetails.setUrl("www.example.com");
        contactDetails.setFurtherDetails("Please do not contact.");

        Contact privateContactDetails = new Contact();
        privateContactDetails.setEmail("private@example.com");

        organisation.setContactDetails(contactDetails);
        organisation.setPrivateContactDetails(privateContactDetails);

        return organisation;
    }
}
