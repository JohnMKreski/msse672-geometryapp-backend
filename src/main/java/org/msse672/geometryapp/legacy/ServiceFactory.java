package org.msse672.geometryapp.legacy;

import org.msse672.geometryapp.service.QuadService;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.lang.reflect.Constructor;

//Used for educational purposes in Week 5 to demonstrate XML and Service config files
//Factory class to read XML configuration, validate it against XSD, and instantiate QuadService implementation
public class ServiceFactory {

    private final String xmlPath;
    private final String xsdPath;

    // Constructor accepts paths to the XML and XSD files
    public ServiceFactory(String xmlPath, String xsdPath) {
        this.xmlPath = xmlPath;
        this.xsdPath = xsdPath;
    }

    // Main method to return a QuadService instance by reading XML and instantiating the class
    public QuadService getQuadService() {
        try {
            System.out.println("[FACTORY] Starting XML parsing and validation...");

            // Step 1: Set up schema factory and load the XSD schema for validation
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            var schema = schemaFactory.newSchema(new File(xsdPath));
            System.out.println("[FACTORY] Loaded XSD: " + xsdPath);

            // Step 2: Set up a DOM parser with validation enabled
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setSchema(schema); // Apply schema for validation
            factory.setNamespaceAware(true); // Needed for schema validation
            factory.setValidating(false); // Only use schema, not DTD validation
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Step 3: Parse the XML file
            Document doc = builder.parse(new File(xmlPath));
            System.out.println("[FACTORY] Successfully parsed XML: " + xmlPath);

            // Step 4: Find all <service> elements
            NodeList services = doc.getElementsByTagName("service");
            System.out.println("[FACTORY] Found " + services.getLength() + " <service> elements.");

            // Loop through <service> entries and find quadService
            for (int i = 0; i < services.getLength(); i++) {
                Element service = (Element) services.item(i);
                if ("quadService".equals(service.getAttribute("name"))) {
                    // Get the class name from the XML
                    String name = service.getAttribute("name");
                    String className = service.getAttribute("class");

                    System.out.println("[FACTORY] Found service: name=" + name + ", class=" + className);

                    // Step 6: Use reflection to load and instantiate the class
                    Class<?> clazz = Class.forName(className);
                    Constructor<?> constructor = clazz.getDeclaredConstructor();
                    Object instance = constructor.newInstance();
                    System.out.println("[FACTORY] Instantiated class: " + clazz.getSimpleName());

                    // Step 7: Check if it implements QuadService and return it
                    if (instance instanceof QuadService) {
                        return (QuadService) instance;
                    } else {
                        throw new IllegalArgumentException("Class " + className + " does not implement QuadService.");
                    }
                }
            }

            throw new RuntimeException("quadService not found in XML.");

        } catch (SAXException e) {
            throw new RuntimeException("XML Validation failed: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load QuadService from XML: " + e.getMessage(), e);
        }
    }
}
