//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.02.10 um 11:10:29 AM CET 
//


package de.mmenning.db.index.evaluation.xml.setup;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für EvaluationSetup complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="EvaluationSetup">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="evalFunction" type="{http://www.mmenning.de/db/index/evaluation/xml/setup}Function"/>
 *         &lt;element name="dim" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded"/>
 *         &lt;element name="bufferSize" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded"/>
 *         &lt;element name="blockSize" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded"/>
 *         &lt;element name="initialSize" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="incSize" type="{http://www.w3.org/2001/XMLSchema}int" maxOccurs="unbounded"/>
 *         &lt;element name="startPercentage" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="updatePercentage" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="endPercentage" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="queries" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="tests" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="spatialDistribution" type="{http://www.mmenning.de/db/index/evaluation/xml/setup}Distribution"/>
 *         &lt;element name="maxSpatialElementSize" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="validTimeDistribution" type="{http://www.mmenning.de/db/index/evaluation/xml/setup}Distribution"/>
 *         &lt;element name="maxValidTimeLength" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="vtInfinityProbability" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="queryDistribution" type="{http://www.mmenning.de/db/index/evaluation/xml/setup}Distribution"/>
 *         &lt;element name="querySize" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="randomSeed" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EvaluationSetup", propOrder = {
    "evalFunction",
    "dim",
    "bufferSize",
    "blockSize",
    "initialSize",
    "incSize",
    "startPercentage",
    "updatePercentage",
    "endPercentage",
    "queries",
    "tests",
    "spatialDistribution",
    "maxSpatialElementSize",
    "validTimeDistribution",
    "maxValidTimeLength",
    "vtInfinityProbability",
    "queryDistribution",
    "querySize",
    "randomSeed"
})
public class EvaluationSetup {

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected Function evalFunction;
    @XmlElement(type = Integer.class)
    protected List<Integer> dim;
    @XmlElement(type = Integer.class)
    protected List<Integer> bufferSize;
    @XmlElement(type = Integer.class)
    protected List<Integer> blockSize;
    protected int initialSize;
    @XmlElement(type = Integer.class)
    protected List<Integer> incSize;
    protected double startPercentage;
    protected double updatePercentage;
    protected double endPercentage;
    protected int queries;
    protected int tests;
    @XmlElement(required = true)
    protected Distribution spatialDistribution;
    protected double maxSpatialElementSize;
    @XmlElement(required = true)
    protected Distribution validTimeDistribution;
    protected double maxValidTimeLength;
    protected double vtInfinityProbability;
    @XmlElement(required = true)
    protected Distribution queryDistribution;
    protected double querySize;
    protected long randomSeed;

    /**
     * Ruft den Wert der evalFunction-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Function }
     *     
     */
    public Function getEvalFunction() {
        return evalFunction;
    }

    /**
     * Legt den Wert der evalFunction-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Function }
     *     
     */
    public void setEvalFunction(Function value) {
        this.evalFunction = value;
    }

    /**
     * Gets the value of the dim property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dim property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDim().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getDim() {
        if (dim == null) {
            dim = new ArrayList<Integer>();
        }
        return this.dim;
    }

    /**
     * Gets the value of the bufferSize property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bufferSize property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBufferSize().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getBufferSize() {
        if (bufferSize == null) {
            bufferSize = new ArrayList<Integer>();
        }
        return this.bufferSize;
    }

    /**
     * Gets the value of the blockSize property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the blockSize property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBlockSize().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getBlockSize() {
        if (blockSize == null) {
            blockSize = new ArrayList<Integer>();
        }
        return this.blockSize;
    }

    /**
     * Ruft den Wert der initialSize-Eigenschaft ab.
     * 
     */
    public int getInitialSize() {
        return initialSize;
    }

    /**
     * Legt den Wert der initialSize-Eigenschaft fest.
     * 
     */
    public void setInitialSize(int value) {
        this.initialSize = value;
    }

    /**
     * Gets the value of the incSize property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the incSize property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIncSize().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integer }
     * 
     * 
     */
    public List<Integer> getIncSize() {
        if (incSize == null) {
            incSize = new ArrayList<Integer>();
        }
        return this.incSize;
    }

    /**
     * Ruft den Wert der startPercentage-Eigenschaft ab.
     * 
     */
    public double getStartPercentage() {
        return startPercentage;
    }

    /**
     * Legt den Wert der startPercentage-Eigenschaft fest.
     * 
     */
    public void setStartPercentage(double value) {
        this.startPercentage = value;
    }

    /**
     * Ruft den Wert der updatePercentage-Eigenschaft ab.
     * 
     */
    public double getUpdatePercentage() {
        return updatePercentage;
    }

    /**
     * Legt den Wert der updatePercentage-Eigenschaft fest.
     * 
     */
    public void setUpdatePercentage(double value) {
        this.updatePercentage = value;
    }

    /**
     * Ruft den Wert der endPercentage-Eigenschaft ab.
     * 
     */
    public double getEndPercentage() {
        return endPercentage;
    }

    /**
     * Legt den Wert der endPercentage-Eigenschaft fest.
     * 
     */
    public void setEndPercentage(double value) {
        this.endPercentage = value;
    }

    /**
     * Ruft den Wert der queries-Eigenschaft ab.
     * 
     */
    public int getQueries() {
        return queries;
    }

    /**
     * Legt den Wert der queries-Eigenschaft fest.
     * 
     */
    public void setQueries(int value) {
        this.queries = value;
    }

    /**
     * Ruft den Wert der tests-Eigenschaft ab.
     * 
     */
    public int getTests() {
        return tests;
    }

    /**
     * Legt den Wert der tests-Eigenschaft fest.
     * 
     */
    public void setTests(int value) {
        this.tests = value;
    }

    /**
     * Ruft den Wert der spatialDistribution-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Distribution }
     *     
     */
    public Distribution getSpatialDistribution() {
        return spatialDistribution;
    }

    /**
     * Legt den Wert der spatialDistribution-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Distribution }
     *     
     */
    public void setSpatialDistribution(Distribution value) {
        this.spatialDistribution = value;
    }

    /**
     * Ruft den Wert der maxSpatialElementSize-Eigenschaft ab.
     * 
     */
    public double getMaxSpatialElementSize() {
        return maxSpatialElementSize;
    }

    /**
     * Legt den Wert der maxSpatialElementSize-Eigenschaft fest.
     * 
     */
    public void setMaxSpatialElementSize(double value) {
        this.maxSpatialElementSize = value;
    }

    /**
     * Ruft den Wert der validTimeDistribution-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Distribution }
     *     
     */
    public Distribution getValidTimeDistribution() {
        return validTimeDistribution;
    }

    /**
     * Legt den Wert der validTimeDistribution-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Distribution }
     *     
     */
    public void setValidTimeDistribution(Distribution value) {
        this.validTimeDistribution = value;
    }

    /**
     * Ruft den Wert der maxValidTimeLength-Eigenschaft ab.
     * 
     */
    public double getMaxValidTimeLength() {
        return maxValidTimeLength;
    }

    /**
     * Legt den Wert der maxValidTimeLength-Eigenschaft fest.
     * 
     */
    public void setMaxValidTimeLength(double value) {
        this.maxValidTimeLength = value;
    }

    /**
     * Ruft den Wert der vtInfinityProbability-Eigenschaft ab.
     * 
     */
    public double getVtInfinityProbability() {
        return vtInfinityProbability;
    }

    /**
     * Legt den Wert der vtInfinityProbability-Eigenschaft fest.
     * 
     */
    public void setVtInfinityProbability(double value) {
        this.vtInfinityProbability = value;
    }

    /**
     * Ruft den Wert der queryDistribution-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Distribution }
     *     
     */
    public Distribution getQueryDistribution() {
        return queryDistribution;
    }

    /**
     * Legt den Wert der queryDistribution-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Distribution }
     *     
     */
    public void setQueryDistribution(Distribution value) {
        this.queryDistribution = value;
    }

    /**
     * Ruft den Wert der querySize-Eigenschaft ab.
     * 
     */
    public double getQuerySize() {
        return querySize;
    }

    /**
     * Legt den Wert der querySize-Eigenschaft fest.
     * 
     */
    public void setQuerySize(double value) {
        this.querySize = value;
    }

    /**
     * Ruft den Wert der randomSeed-Eigenschaft ab.
     * 
     */
    public long getRandomSeed() {
        return randomSeed;
    }

    /**
     * Legt den Wert der randomSeed-Eigenschaft fest.
     * 
     */
    public void setRandomSeed(long value) {
        this.randomSeed = value;
    }

}
