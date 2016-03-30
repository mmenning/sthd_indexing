//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2014.08.13 um 12:40:15 PM CEST 
//


package de.mmenning.db.index.evaluation.xml.index;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the de.mmenning.db.index.evaluation.xml.index package. 
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

    private final static QName _Index_QNAME = new QName("http://www.mmenning.de/db/index/evaluation/xml/index", "index");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.mmenning.db.index.evaluation.xml.index
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link IndexBase }
     * 
     */
    public IndexBase createIndexBase() {
        return new IndexBase();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link IndexBase }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.mmenning.de/db/index/evaluation/xml/index", name = "index")
    public JAXBElement<IndexBase> createIndex(IndexBase value) {
        return new JAXBElement<IndexBase>(_Index_QNAME, IndexBase.class, null, value);
    }

}
