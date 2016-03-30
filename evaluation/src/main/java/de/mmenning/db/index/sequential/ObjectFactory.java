//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.02.01 um 09:36:01 AM CET 
//


package de.mmenning.db.index.sequential;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the de.mmenning.db.index.sequential package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Access_QNAME = new QName("http://www.mmenning.de/db/index/sequential", "access");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.mmenning.db.index.sequential
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AdaptedSequentialAccess }
     * 
     */
    public AdaptedSequentialAccess createAdaptedSequentialAccess() {
        return new AdaptedSequentialAccess();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AdaptedSequentialAccess }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.mmenning.de/db/index/sequential", name = "access")
    public JAXBElement<AdaptedSequentialAccess> createAccess(AdaptedSequentialAccess value) {
        return new JAXBElement<AdaptedSequentialAccess>(_Access_QNAME, AdaptedSequentialAccess.class, null, value);
    }

}
