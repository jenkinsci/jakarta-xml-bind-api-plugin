package io.jenkins.plugins.jakarta_xml_bind_api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import jenkins.util.SetContextClassLoader;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.RealJenkinsRule;

public class JAXBContextTest {

    @Rule
    public RealJenkinsRule rr = new RealJenkinsRule();

    @Test
    public void smokes() throws Throwable {
        rr.then(JAXBContextTest::_smokes);
    }

    private static void _smokes(JenkinsRule r) throws Throwable {
        Book book = new Book();
        book.setId(1L);
        book.setName("Guide to JAXB");
        JAXBContext context;
        try (SetContextClassLoader sccl = new SetContextClassLoader(RealJenkinsRule.Endpoint.class)) {
            context = JAXBContext.newInstance(Book.class);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        context.createMarshaller().marshal(book, baos);
        String xml = baos.toString(StandardCharsets.US_ASCII.name());
        assertThat(xml, containsString("<book id=\"1\"><title>Guide to JAXB</title></book>"));
        Book book2 = (Book) context.createUnmarshaller()
                .unmarshal(new ByteArrayInputStream(xml.getBytes(StandardCharsets.US_ASCII)));
        assertEquals(book.getId(), book2.getId());
        assertEquals(book.getName(), book2.getName());
    }

    @XmlRootElement(name = "book")
    @XmlType(propOrder = {"id", "name"})
    static class Book {
        private Long id;
        private String name;

        @XmlAttribute
        public void setId(Long id) {
            this.id = id;
        }

        @XmlElement(name = "title")
        public void setName(String name) {
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
