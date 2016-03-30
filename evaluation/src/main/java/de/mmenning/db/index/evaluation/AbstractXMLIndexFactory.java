package de.mmenning.db.index.evaluation;

import de.mmenning.db.index.NDRectangleKeyIndex;
import de.mmenning.db.index.NowGen;
import de.mmenning.db.index.SpatioTemporalIndexFactory;
import de.mmenning.db.storage.StorageManager;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.File;

public abstract class AbstractXMLIndexFactory<E> implements
      SpatioTemporalIndexFactory {

   protected final E object;

   public AbstractXMLIndexFactory(String xmlFile, Class<E> clss) throws SAXException,
         JAXBException {
      object = unmarshal(xmlFile, clss);
   }

   protected E unmarshal(String xmlFile, Class<E> clss) throws SAXException,
         JAXBException {
      JAXBContext jbc = JAXBContext.newInstance(clss
            .getPackage().getName());
      JAXBElement jelem = (JAXBElement) jbc.createUnmarshaller().unmarshal(
            new File(xmlFile));

      return (E) jelem.getValue();

   }

   public abstract NDRectangleKeyIndex createIndex(int dims, int blockSize,
                                                   StorageManager s, NowGen now);

}
