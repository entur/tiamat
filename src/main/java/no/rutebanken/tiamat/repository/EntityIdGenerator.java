package no.rutebanken.tiamat.repository;

import no.rutebanken.tiamat.model.Quay;
import no.rutebanken.tiamat.model.StopPlace;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EntityIdGenerator implements IdentifierGenerator {

    private static final Logger logger = LoggerFactory.getLogger(EntityIdGenerator.class);

    @Override
    public Serializable generate(SessionImplementor session, Object object) throws HibernateException {
        String prefix = determinePrefix(object);

        Connection connection = session.connection();
        PreparedStatement preparedStatement = null;
        try {

            preparedStatement = connection
                    .prepareStatement("SELECT nextval ('entity_sequence') as nextval");

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                int nextval = rs.getInt("nextval");
                String id = prefix + "-" + nextval;
                logger.trace("Generated id: {}", id);
                return id;
            }

        } catch (SQLException e) {
            throw new HibernateException("Unable to generate ID", e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (Throwable e) {
                }
            }
        }
        return null;
    }

    private String determinePrefix(Object object) {
        if(object instanceof StopPlace) {
            return "sp";
        } else if (object instanceof Quay) {
            return "q";
        }
        return "";
    }
}
