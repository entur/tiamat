package org.rutebanken.tiamat.config;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/**
 * Util class to retrieve localized messages.
 */
@Component
public class Messages {

    public static final String VALIDATION_FROM_DATE_AFTER_TO_DATE = "validation.from-date-after-to-date";
    public static final String VALIDATION_FROM_DATE_NOT_SET = "validation.from-date-not-set";
    public static final String VALIDATION_TO_DATE_AFTER_NEXT_VERSION_FROM_DATE = "validation.existing-version-to-date-after-new-version-from-date";
    public static final String VALIDATION_FROM_DATE_AFTER_NEXT_VERSION_FROM_DATE = "validation.existing-version-from-date-after-new-version-from-date";
    public static final String VALIDATION_CANNOT_TERMINATE_FOR_NULL = "validation.cannot-terminate-for-null";

    MessageSourceAccessor accessor;


    /**
     * @param messageSource
     */
    public Messages(MessageSource messageSource) {
        this.accessor = new MessageSourceAccessor(messageSource, LocaleContextHolder.getLocale());
    }


    /**
     * Get a localized message by its key.
     *
     * @param messageKey       message messageKey.
     * @param messageArguments optional arguments needed to build message
     * @return
     */
    public String get(String messageKey, Object... messageArguments) {
        return accessor.getMessage(messageKey, messageArguments);
    }



}
