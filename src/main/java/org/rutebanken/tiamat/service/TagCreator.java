package org.rutebanken.tiamat.service;

import com.google.common.collect.Sets;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.auth.UsernameFetcher;
import org.rutebanken.tiamat.model.EntityInVersionStructure;
import org.rutebanken.tiamat.model.VersionOfObjectRefStructure;
import org.rutebanken.tiamat.model.tag.Tag;
import org.rutebanken.tiamat.repository.reference.ReferenceResolver;
import org.rutebanken.tiamat.repository.TagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.regex.Pattern;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;

@Service
public class TagCreator {

    private static final Logger logger = LoggerFactory.getLogger(TagRemover.class);

    private static final String TAG_NAME_REGEX = "^[\\w\\dæøåÆØÅ]*$";
    private static final Pattern tagPattern = Pattern.compile(TAG_NAME_REGEX, Pattern.UNICODE_CASE);

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UsernameFetcher usernameFetcher;

    @Autowired
    private ReferenceResolver referenceResolver;

    @Autowired
    private ReflectionAuthorizationService authorizationService;

    public Tag createTag(String tagName, String idReference, String comment) {

        if(!tagPattern.matcher(tagName).matches()) {
            throw new IllegalArgumentException("Tag name not valid. Should not contain spaces or special characters. Only characters and or numbers: " + TAG_NAME_REGEX);
        }
        tagName = tagName.toLowerCase();

        Tag tag = tagRepository.findByNameAndIdReference(tagName, idReference);
        boolean brandNew = false;
        if(tag == null) {
            brandNew = true;

            // Check if the tag already exists
            EntityInVersionStructure entityInVersionStructure = referenceResolver.resolve(new VersionOfObjectRefStructure(idReference));
            authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Sets.newHashSet(entityInVersionStructure));
            if(entityInVersionStructure == null) {
                throw new IllegalArgumentException("The referenced entity does not exist: " + idReference);
            }

            logger.info("Found entity from reference: {}", entityInVersionStructure);

            tag = new Tag();
            tag.setIdreference(idReference);
            tag.setName(tagName);

        } else {
            // If the tag with this name for this entity ref was previously removed. Clear removed fields.
            tag.setRemovedBy(null);
            tag.setRemoved(null);
        }

        tag.setCreatedBy(usernameFetcher.getUserNameForAuthenticatedUser());
        tag.setComment(comment);
        // If tag was removed, and then recreated, reset created date. We do not have versioning for tags.
        tag.setCreated(Instant.now());

        tag = tagRepository.save(tag);
        logger.info("Created tag {}{}", tag, brandNew ? "" : " Updated previously created tag");
        return tag;
    }

}
