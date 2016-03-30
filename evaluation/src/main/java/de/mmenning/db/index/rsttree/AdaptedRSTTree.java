//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2014.09.05 um 03:29:09 PM CEST 
//


package de.mmenning.db.index.rsttree;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für AdaptedRSTTree complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="AdaptedRSTTree">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="alpha" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="alphaW" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="maxK" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AdaptedRSTTree")
public class AdaptedRSTTree {

    @XmlAttribute(name = "alpha", required = true)
    protected double alpha;
    @XmlAttribute(name = "alphaW", required = true)
    protected double alphaW;
    @XmlAttribute(name = "maxK")
    protected Integer maxK;

    /**
     * Ruft den Wert der alpha-Eigenschaft ab.
     * 
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * Legt den Wert der alpha-Eigenschaft fest.
     * 
     */
    public void setAlpha(double value) {
        this.alpha = value;
    }

    /**
     * Ruft den Wert der alphaW-Eigenschaft ab.
     * 
     */
    public double getAlphaW() {
        return alphaW;
    }

    /**
     * Legt den Wert der alphaW-Eigenschaft fest.
     * 
     */
    public void setAlphaW(double value) {
        this.alphaW = value;
    }

    /**
     * Ruft den Wert der maxK-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxK() {
        return maxK;
    }

    /**
     * Legt den Wert der maxK-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxK(Integer value) {
        this.maxK = value;
    }

}
