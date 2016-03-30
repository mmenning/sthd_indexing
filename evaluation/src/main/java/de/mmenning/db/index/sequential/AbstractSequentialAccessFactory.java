package de.mmenning.db.index.sequential;

import de.mmenning.db.index.NDRectangleKeyIndex;
import de.mmenning.db.index.NowGen;
import de.mmenning.db.index.evaluation.AbstractXMLIndexFactory;
import de.mmenning.db.storage.StorageManager;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;

/**
 * @author Mathias Menninghaus (mathias.mennighaus@uos.de)
 */
public class AbstractSequentialAccessFactory extends
      AbstractXMLIndexFactory<AdaptedSequentialAccess> {

   public AbstractSequentialAccessFactory(String xmlFile) throws
         JAXBException, SAXException {
      super(xmlFile, AdaptedSequentialAccess.class);
   }

   @Override
   public NDRectangleKeyIndex createIndex(int dims, int blockSize, StorageManager s, NowGen now) {
      return new SequentialAccess(dims, s, now);
   }
}
