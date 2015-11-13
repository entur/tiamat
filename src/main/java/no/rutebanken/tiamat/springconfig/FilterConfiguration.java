package no.rutebanken.tiamat.springconfig;


import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.support.OpenSessionInViewFilter;

import javax.persistence.EntityManagerFactory;
import javax.servlet.Filter;
import java.util.Arrays;

@Configuration
public class FilterConfiguration {
/*
    @Bean
    public FilterRegistrationBean filterRegistrationBean(EntityManagerFactory entityManagerFactory) {

        OpenSessionInViewFilter openSessionInViewFilter = new OpenSessionInViewFilter();

        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(openSessionInViewFilter);
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(Integer.MAX_VALUE);

        return filterRegistrationBean;

    }

    @Bean(name = "sessionFactory")
    SessionFactory sessionFactory(EntityManagerFactory entityManagerFactory) {

        if(entityManagerFactory.unwrap(SessionFactory.class) == null){
            throw new NullPointerException("entityManagerFactory is not a hibernate factory");
        }

        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        return sessionFactory;
    }
 */

}
