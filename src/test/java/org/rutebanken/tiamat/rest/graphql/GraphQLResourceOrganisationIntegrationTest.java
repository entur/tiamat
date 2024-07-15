package org.rutebanken.tiamat.rest.graphql;

import org.junit.Test;
import org.rutebanken.tiamat.model.Contact;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Organisation;
import org.rutebanken.tiamat.model.OrganisationTypeEnumeration;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

public class GraphQLResourceOrganisationIntegrationTest extends AbstractGraphQLResourceIntegrationTest {

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
              }
            }""".formatted(organisation.getNetexId());
            // TODO: contactDetails
            // TODO: privateContactDetails

        executeGraphqQLQueryOnly(graphQlJsonQuery)
                .body("data.organisation", hasSize(1))
                .body("data.organisation[0].id", equalTo(organisation.getNetexId()))
                .body("data.organisation[0].privateCode", equalTo(organisation.getPrivateCode()))
                .body("data.organisation[0].companyNumber", equalTo(organisation.getCompanyNumber()))
                .body("data.organisation[0].name", equalTo(organisation.getName()))
                .body("data.organisation[0].organisationType", equalTo(organisation.getOrganisationType().value()))
                .body("data.organisation[0].legalName.value", equalTo(organisation.getLegalName().getValue()))
                .body("data.organisation[0].legalName.lang", equalTo(organisation.getLegalName().getLang()))
                ;
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
              }
            }""";
            // TODO: contactDetails
            // TODO: privateContactDetails

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
              }
            }""".formatted(organisation.getNetexId());
            // TODO: contactDetails
            // TODO: privateContactDetails

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
                ;
    }

// TODO
//    @Test
//    public void testDeleteOrganisation() {
//    }

    protected Organisation createTestOrganisation() {
        Organisation organisation = new Organisation();
        organisation.setPrivateCode("PrivCode");
        organisation.setCompanyNumber("112233");
        organisation.setName("Test Organisation");
        organisation.setOrganisationType(OrganisationTypeEnumeration.OTHER);
        organisation.setLegalName(new EmbeddableMultilingualString("Test Organisation Oy", "fi"));

    /*
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
    */

        return organisation;
    }
}
