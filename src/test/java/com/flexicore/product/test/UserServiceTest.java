package com.flexicore.product.test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flexicore.data.jsoncontainers.*;
import com.flexicore.product.containers.request.EventFiltering;
import com.flexicore.product.model.Event;
import com.flexicore.product.rest.IEventRESTService;
import com.flexicore.rest.interfaces.IAuthenticationRESTService;
import com.flexicore.security.AuthenticationRequestHolder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@RunWith(Arquillian.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class UserServiceTest {


    @ArquillianResource
    private URL deploymentURL;
    private ResteasyWebTarget target;


    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        File[] files = Maven.resolver()
                .loadPomFromFile("pom.xml")
                .importCompileAndRuntimeDependencies()
                .resolve()
                .withTransitivity()
                .asFile();

        File[] entites = new File("/home/flexicore/entities").listFiles();
        File persistenceXml = new File(UserServiceTest.class.getClassLoader().getResource("test-persistence.xml").getFile());
        if (entites != null && entites.length != 0) {
            files = concatenate(files, entites);
            for (File entity : entites) {
                try {
                    addJarToPersistenceXML(entity, persistenceXml);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        WebArchive war =
                ShrinkWrap.create(ZipImporter.class, UUID.randomUUID().toString() + ".war")
                        .importFrom(new File("C:\\Users\\asaf\\dev\\flexicore\\testing\\FlexiCore.war.zip"))
                        .as(WebArchive.class)

                        .addAsLibraries(files)
                        .addAsResource("test-beans.xml", "META-INF/beans.xml")
                        .addAsResource(persistenceXml, "META-INF/persistence.xml");
        System.out.println(war.toString(true));
        return war;

    }

    public static <T> T[] concatenate(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }


    public static boolean addJarToPersistenceXML(File file, File persistenceXml) throws IOException {
        JarFile jar = new JarFile(file);
        Enumeration<JarEntry> entries = jar.entries();
        JarEntry entry = null;
        File classAtClassPath = null;
        boolean classPathUpdated = false;
        Map<String, Set<String>> classNameToPersistenceUnitName = new HashMap<>();


        while (entries.hasMoreElements()) {
            entry = entries.nextElement();
            if (entry.getName().endsWith("persistence.xml")) {
                try (InputStream is = jar.getInputStream(entry)) {
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(is);
                    Element d = doc.getDocumentElement();
                    NodeList l = d.getElementsByTagName("persistence-unit");
                    for (int i = 0; i < l.getLength(); i++) {
                        Node n = l.item(i);
                        String persistenceUnitName = l.item(i).getAttributes().getNamedItem("name").getNodeValue();
                        NodeList classes = ((Element) n).getElementsByTagName("class");

                        for (int j = 0; j < classes.getLength(); j++) {
                            String className = classes.item(j).getFirstChild().getNodeValue();
                            Set<String> persistenceUnitsForClass = classNameToPersistenceUnitName.computeIfAbsent(persistenceUnitName, f -> new HashSet<>());
                            persistenceUnitsForClass.add(className);
                        }

                    }
                } catch (ParserConfigurationException | SAXException e) {
                    e.printStackTrace();
                }


            }

        }
        jar.close();
        try {
            if (!classNameToPersistenceUnitName.isEmpty()) {
                for (Map.Entry<String, Set<String>> persistenceUnitToClasses : classNameToPersistenceUnitName.entrySet()) {
                    addClassToPersistenceXml(persistenceUnitToClasses.getValue(), persistenceUnitToClasses.getKey(), persistenceXml);
                }

            }

        } catch (ParserConfigurationException | SAXException | TransformerException e) {
            e.printStackTrace();
        }
        return classPathUpdated;

    }


    private static void addClassToPersistenceXml(Set<String> toAdd, String persistenceUnitName, File persistenceXml) throws ParserConfigurationException, IOException, SAXException, TransformerException {


        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(persistenceXml);
        Element d = doc.getDocumentElement();
        NodeList l = d.getElementsByTagName("persistence-unit");

        int selected = -1;

        for (int i = 0; i < l.getLength(); i++) {
            if (l.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(persistenceUnitName)) {
                selected = i;
                break;
            }
        }
        if (selected == -1) {
            System.out.println(("WARNING-could not find persistence unit named " + persistenceUnitName + " at " + persistenceXml.getAbsolutePath()));
            return;
        }
        Node n = l.item(selected);
        l = ((Element) n).getElementsByTagName("class");
        Node exlcudeTag = ((Element) n).getElementsByTagName("exclude-unlisted-classes").item(0);

        for (int j = 0; j < l.getLength(); j++) {
            toAdd.remove(l.item(j).getFirstChild().getNodeValue());
        }
        for (String s : toAdd) {
            Element el = doc.createElement("class");
            el.appendChild(doc.createTextNode(s));
            n.insertBefore(el, (Node) exlcudeTag);
            //n.appendChild(el);
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(persistenceXml);
        // creating output stream
        transformer.transform(source, result);

    }

    private static ObjectMapper configureObjectMapper(ClassLoader classLoader, ObjectMapper mapper) {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);

        SimpleModule mod = new SimpleModule();
        FieldSetContainerDeserializer<? extends Serializable> s = new FieldSetContainerDeserializer<>();
        mod.addDeserializer(FieldSetContainer.class, s);

        mapper.registerModule(mod);
        JavaTimeModule module = new JavaTimeModule();
        mapper.registerModule(module);
        SimpleModule dateModule = new SimpleModule("date module");
        dateModule.addDeserializer(LocalDateTime.class, new JsonDateDeserializer("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
        dateModule.addSerializer(LocalDateTime.class, new JsonDateSerializer());
        mapper.registerModule(dateModule);

        if (classLoader != null) {
            TypeFactory tf = TypeFactory.defaultInstance().withClassLoader(classLoader);
            mapper.setTypeFactory(tf);
            System.out.println("Created Object Mapper For " + classLoader);


        } else {
            System.out.println("Created Default Object Mapper");
        }
        return mapper;
    }

    private ResteasyClient client;

    @Before
    public void initClient() throws URISyntaxException, MalformedURLException {
        if(client==null){
            ResteasyJackson2Provider resteasyJackson2Provider = new ResteasyJackson2Provider();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper = configureObjectMapper(UserServiceTest.class.getClassLoader(), objectMapper);
            client = new ResteasyClientBuilder()
                    .register(resteasyJackson2Provider)
                    .build();
            target = client.target(new URL(deploymentURL,"rest").toURI());
        }
    }


    @Test
    public void aCreateEquipment() {
        IAuthenticationRESTService authenticationRESTService = target.proxy(IAuthenticationRESTService.class);
        String key = authenticationRESTService.login("", new AuthenticationRequestHolder().setMail("admin@flexicore.com").setPassword("admin")).getAuthenticationkey();
        IEventRESTService iEventRESTService = target.proxy(IEventRESTService.class);
        PaginationResponse<Event> res = iEventRESTService.getAllEvents(key, new EventFiltering().setPageSize(10).setCurrentPage(0), null);
        System.out.println(res.getTotalRecords());
    }


}
