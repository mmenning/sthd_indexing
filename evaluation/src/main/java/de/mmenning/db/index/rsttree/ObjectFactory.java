//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2014.09.05 um 03:29:09 PM CEST 
//


package de.mmenning.db.index.rsttree;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the de.mmenning.db.index.rsttree package. 
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

    private final static QName _Tree_QNAME = new QName("http://www.mmenning.de/db/index/rsttree", "tree");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.mmenning.db.index.rsttree
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AdaptedRSTTree }
     * 
     */
    public AdaptedRSTTree createAdaptedRSTTree() {
        return new AdaptedRSTTree();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AdaptedRSTTree }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.mmenning.de/db/index/rsttree", name = "tree")
    public JAXBElement<AdaptedRSTTree> createTree(AdaptedRSTTree value) {
        return new JAXBElement<AdaptedRSTTree>(_Tree_QNAME, AdaptedRSTTree.class, null, value);
    }

}
