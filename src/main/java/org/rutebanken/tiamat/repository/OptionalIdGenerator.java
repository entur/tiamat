package org.rutebanken.tiamat.repository;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentityGenerator;
import org.rutebanken.tiamat.model.EntityStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class OptionalIdGenerator extends IdentityGenerator {

    private static final Logger logger = LoggerFactory.getLogger(OptionalIdGenerator.class);

    @Override
    public Serializable generate(SessionImplementor session, Object obj) throws HibernateException {

        if (obj == null) throw new HibernateException(new NullPointerException());

        if ((((EntityStructure) obj).getId()) == null) {
            Serializable id = super.generate(session, obj);
            return id;
        } else {
            return ((EntityStructure) obj).getId();
        }
    }
}