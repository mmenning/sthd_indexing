//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.02.10 um 11:10:29 AM CET 
//


package de.mmenning.db.index.evaluation.xml.setup;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the de.mmenning.db.index.evaluation.xml.setup package. 
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

    private final static QName _Setup_QNAME = new QName("http://www.mmenning.de/db/index/evaluation/xml/setup", "setup");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.mmenning.db.index.evaluation.xml.setup
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EvaluationSetup }
     * 
     */
    public EvaluationSetup createEvaluationSetup() {
        return new EvaluationSetup();
    }

    /**
     * Create an instance of {@link Cluster }
     * 
     */
    public Cluster createCluster() {
        return new Cluster();
    }

    /**
     * Create an instance of {@link Uniform }
     * 
     */
    public Uniform createUniform() {
        return new Uniform();
    }

    /**
     * Create an instance of {@link Skewed }
     * 
     */
    public Skewed createSkewed() {
        return new Skewed();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EvaluationSetup }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.mmenning.de/db/index/evaluation/xml/setup", name = "setup")
    public JAXBElement<EvaluationSetup> createSetup(EvaluationSetup value) {
        return new JAXBElement<EvaluationSetup>(_Setup_QNAME, EvaluationSetup.class, null, value);
    }

}
