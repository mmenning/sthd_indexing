package de.mmenning.db.index.rsttree;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import de.mmenning.db.index.NDRectangleKeyIndex;
import de.mmenning.db.index.NowGen;
import de.mmenning.db.index.evaluation.AbstractXMLIndexFactory;
import de.mmenning.db.storage.StorageManager;

public class AbstractRSTTreeFactory extends
		AbstractXMLIndexFactory<AdaptedRSTTree> {

	public AbstractRSTTreeFactory(String xmlFile) throws SAXException,
			JAXBException {
		super(xmlFile, AdaptedRSTTree.class);
	}

	@Override
	public NDRectangleKeyIndex createIndex(int dims, int blockSize,
			StorageManager s, NowGen now) {
		// if (object.maxK != null) {
		// return RSTTreeFactory.createRSTTreeConstP(object.maxK, dims - 2,
		// object.alpha, object.constP, now, s);
		// } else {
		// return RSTTreeFactory.createRSTTreeWithConstantP(blockSize,
		// dims - 2, object.alpha, object.constP, now, s);
		// }

		if (object.maxK == null) {
			return RSTTreeFactory.createRSTTreeWithTimeHorizon(blockSize,
					dims - 2, object.alpha, object.alphaW, now, s);
		} else {

			return RSTTreeFactory.createRSTTreeTimeH(object.maxK, dims - 2,
					object.alpha, object.alphaW, now, s);

		}
	}
}
