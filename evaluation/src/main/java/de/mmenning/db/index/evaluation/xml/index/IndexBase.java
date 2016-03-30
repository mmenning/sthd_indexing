//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2014.08.13 um 12:40:15 PM CEST 
//


package de.mmenning.db.index.evaluation.xml.index;

import javax.xml.bind.annotation.*;


/**
 * <p>Java-Klasse für IndexBase complex type.
 * <p>
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * <p>
 * <pre>
 * &lt;complexType name="IndexBase">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="index" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="factoryGen" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IndexBase", propOrder = {
      "index",
      "factoryGen"
})
@XmlRootElement(name = "IndexBase")
public class IndexBase {

   @XmlElement(required = true)
   protected String index;
   @XmlElement(required = true)
   protected String factoryGen;

   /**
    * Ruft den Wert der index-Eigenschaft ab.
    *
    * @return possible object is
    * {@link String }
    */
   public String getIndex() {
      return index;
   }

   /**
    * Legt den Wert der index-Eigenschaft fest.
    *
    * @param value allowed object is
    *              {@link String }
    */
   public void setIndex(String value) {
      this.index = value;
   }

   /**
    * Ruft den Wert der factoryGen-Eigenschaft ab.
    *
    * @return possible object is
    * {@link String }
    */
   public String getFactoryGen() {
      return factoryGen;
   }

   /**
    * Legt den Wert der factoryGen-Eigenschaft fest.
    *
    * @param value allowed object is
    *              {@link String }
    */
   public void setFactoryGen(String value) {
      this.factoryGen = value;
   }

}
