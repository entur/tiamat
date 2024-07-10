package org.rutebanken.tiamat.repository;

import org.junit.Test;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.Contact;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Organisation;
import org.rutebanken.tiamat.model.OrganisationTypeEnumeration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OrganisationRepositoryTest extends TiamatIntegrationTest {
    @Test
    public void insertOrganisation() {
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

        organisationRepository.save(organisation);

        List<Organisation> organisations = organisationRepository.findAll();
        assertThat(organisations).hasSize(1);

        Organisation insertedOrganisation = organisations.get(0);
        assertThat(insertedOrganisation.getPrivateCode()).isEqualTo("PrivCode");
        assertThat(insertedOrganisation.getCompanyNumber()).isEqualTo("112233");
        assertThat(insertedOrganisation.getName()).isEqualTo("Test Organisation");
        assertThat(insertedOrganisation.getOrganisationType()).isEqualTo(OrganisationTypeEnumeration.OTHER);
        assertThat(insertedOrganisation.getLegalName().toString()).isEqualTo("Test Organisation Oy (fi)");
        assertThat(insertedOrganisation.getContactDetails().getContactPerson()).isEqualTo("Test Person");
        assertThat(insertedOrganisation.getContactDetails().getEmail()).isEqualTo("null@example.com");
        assertThat(insertedOrganisation.getContactDetails().getPhone()).isEqualTo("+358501234567");
        assertThat(insertedOrganisation.getContactDetails().getFax()).isEqualTo("+358507654321");
        assertThat(insertedOrganisation.getContactDetails().getUrl()).isEqualTo("www.example.com");
        assertThat(insertedOrganisation.getContactDetails().getFurtherDetails()).isEqualTo("Please do not contact.");
        assertThat(insertedOrganisation.getPrivateContactDetails().getContactPerson()).isNull();
        assertThat(insertedOrganisation.getPrivateContactDetails().getEmail()).isEqualTo("private@example.com");
    }
}
