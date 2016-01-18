package no.rutebanken.tiamat.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.jaxb.XmlJaxbAnnotationIntrospector;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonXmlMapperConfig {

    @Bean
    public XmlMapper xmlMapper() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.setAnnotationIntrospector(new XmlJaxbAnnotationIntrospector(xmlMapper.getTypeFactory()));
        xmlMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        xmlMapper.getSerializationConfig()
                .with(new JaxbAnnotationIntrospector(xmlMapper.getTypeFactory()))
                .with(new XmlJaxbAnnotationIntrospector(xmlMapper.getTypeFactory()));

        return xmlMapper;
    }
}
