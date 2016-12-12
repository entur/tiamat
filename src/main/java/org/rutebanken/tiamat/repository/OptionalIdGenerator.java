package org.rutebanken.tiamat.repository;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.SequenceGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.rutebanken.tiamat.model.EntityStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Generate ID if not already set.
 * If already set, it would use the same ID again!
 */
public class OptionalIdGenerator extends SequenceStyleGenerator {

    private static final Logger logger = LoggerFactory.getLogger(OptionalIdGenerator.class);

    @Override
    public Serializable generate(SessionImplementor session, Object obj) throws HibernateException {

        if (obj == null) throw new HibernateException(new NullPointerException());

        if ((((EntityStructure) obj).getId()) == null) {
            logger.info("{} does not already have ID. Generating it.", obj);
            Serializable id = super.generate(session, obj);
            logger.info("Generated id {}", id);
            return id;
        } else {
            logger.info("{} does already have ID. Trying to insert it", obj);
            return ((EntityStructure) obj).getId();
        }
    }
}