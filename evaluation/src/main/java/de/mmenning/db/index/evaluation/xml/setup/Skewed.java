//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.02.10 um 11:10:29 AM CET 
//


package de.mmenning.db.index.evaluation.xml.setup;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für Skewed complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="Skewed">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.mmenning.de/db/index/evaluation/xml/setup}Distribution">
 *       &lt;sequence>
 *         &lt;element name="stDev" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="mean" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="skew" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Skewed", propOrder = {
    "stDev",
    "mean",
    "skew"
})
public class Skewed
    extends Distribution
{

    protected double stDev;
    protected double mean;
    protected double skew;

    /**
     * Ruft den Wert der stDev-Eigenschaft ab.
     * 
     */
    public double getStDev() {
        return stDev;
    }

    /**
     * Legt den Wert der stDev-Eigenschaft fest.
     * 
     */
    public void setStDev(double value) {
        this.stDev = value;
    }

    /**
     * Ruft den Wert der mean-Eigenschaft ab.
     * 
     */
    public double getMean() {
        return mean;
    }

    /**
     * Legt den Wert der mean-Eigenschaft fest.
     * 
     */
    public void setMean(double value) {
        this.mean = value;
    }

    /**
     * Ruft den Wert der skew-Eigenschaft ab.
     * 
     */
    public double getSkew() {
        return skew;
    }

    /**
     * Legt den Wert der skew-Eigenschaft fest.
     * 
     */
    public void setSkew(double value) {
        this.skew = value;
    }

}
