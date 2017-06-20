package org.rutebanken.tiamat.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.jms.ConnectionFactory;
import javax.persistence.EntityManagerFactory;

/**
 * Configure a chained transaction manager for best effort multi resource transactions (db + jms).
 * <p>
 * Db transactions will be commited first.
 */
@Configuration
@ConditionalOnProperty(name = "changelog.publish.enabled", havingValue = "true", matchIfMissing = true)
public class ChainedTransactionConfiguration {

    @Bean
    public JmsTransactionManager jmsTransactionManager(ConnectionFactory connectionFactory) {
        return new JmsTransactionManager(connectionFactory);
    }

    @Bean
    public JpaTransactionManager dataSourceTransactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(JpaTransactionManager jpaTransactionManager, JmsTransactionManager jmsTransactionManager) {
        return new ChainedTransactionManager(jmsTransactionManager, jpaTransactionManager);
    }
}