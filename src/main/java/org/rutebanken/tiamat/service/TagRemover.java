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

package org.rutebanken.tiamat.service;

import com.google.common.collect.Sets;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.model.DataManagedObjectStructure;
import org.rutebanken.tiamat.model.VersionOfObjectRefStructure;
import org.rutebanken.tiamat.model.tag.Tag;
import org.rutebanken.tiamat.repository.reference.ReferenceResolver;
import org.rutebanken.tiamat.repository.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;

@Service
@Transactional
public class TagRemover {

    private static final Logger logger = LoggerFactory.getLogger(TagRemover.class);

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UsernameFetcher usernameFetcher;

    @Autowired
    private ReferenceResolver referenceResolver;

    @Autowired
    private ReflectionAuthorizationService authorizationService;

    public Tag removeTag(String tagName, String idReference, String comment) {
        tagName = tagName.toLowerCase().trim();
        Tag tag = tagRepository.findByNameAndIdReference(tagName, idReference);

        if(tag == null) {
            throw new IllegalArgumentException("Cannot find tag with name " + tagName + " and id reference: " + idReference);
        }

        DataManagedObjectStructure dataManagedObjectStructure = referenceResolver.resolve(new VersionOfObjectRefStructure(idReference));
        authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Sets.newHashSet(dataManagedObjectStructure));

        tag.setComment(comment);
        tag.setRemoved(Instant.now());
        tag.setRemovedBy(usernameFetcher.getUserNameForAuthenticatedUser());

        logger.info("Removed tag {}", tag);
        return tagRepository.save(tag);
    }
}
