//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2014.08.20 um 04:13:45 PM CEST 
//


package de.mmenning.db.index.rtptree;

import javax.xml.bind.annotation.*;


/**
 * <p>Java-Klasse für AdaptedRTPTree complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType name="AdaptedRTPTree">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="medianTT" type="{http://www.mmenning.de/db/index/rtptree}Median"/>
 *         &lt;element name="medianVT" type="{http://www.mmenning.de/db/index/rtptree}Median"/>
 *         &lt;element name="median" type="{http://www.mmenning.de/db/index/rtptree}Median" minOccurs="0"/>
 *         &lt;element name="nodeSize_D" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AdaptedRTPTree", propOrder = {
      "medianTT",
      "medianVT",
      "median",
      "nodeSizeD"
})
@XmlRootElement(name = "AdaptedRTPTree")
public class AdaptedRTPTree {

   @XmlElement(required = true)
   protected Median medianTT;
   @XmlElement(required = true)
   protected Median medianVT;
   protected Median median;
   @XmlElement(name = "nodeSize_D")
   protected Integer nodeSizeD;

   /**
    * Ruft den Wert der medianTT-Eigenschaft ab.
    *
    * @return possible object is
    * {@link Median }
    */
   public Median getMedianTT() {
      return medianTT;
   }

   /**
    * Legt den Wert der medianTT-Eigenschaft fest.
    *
    * @param value allowed object is
    *              {@link Median }
    */
   public void setMedianTT(Median value) {
      this.medianTT = value;
   }

   /**
    * Ruft den Wert der medianVT-Eigenschaft ab.
    *
    * @return possible object is
    * {@link Median }
    */
   public Median getMedianVT() {
      return medianVT;
   }

   /**
    * Legt den Wert der medianVT-Eigenschaft fest.
    *
    * @param value allowed object is
    *              {@link Median }
    */
   public void setMedianVT(Median value) {
      this.medianVT = value;
   }

   /**
    * Ruft den Wert der median-Eigenschaft ab.
    *
    * @return possible object is
    * {@link Median }
    */
   public Median getMedian() {
      return median;
   }

   /**
    * Legt den Wert der median-Eigenschaft fest.
    *
    * @param value allowed object is
    *              {@link Median }
    */
   public void setMedian(Median value) {
      this.median = value;
   }

   /**
    * Ruft den Wert der nodeSizeD-Eigenschaft ab.
    *
    * @return possible object is
    * {@link Integer }
    */
   public Integer getNodeSizeD() {
      return nodeSizeD;
   }

   /**
    * Legt den Wert der nodeSizeD-Eigenschaft fest.
    *
    * @param value allowed object is
    *              {@link Integer }
    */
   public void setNodeSizeD(Integer value) {
      this.nodeSizeD = value;
   }

}
