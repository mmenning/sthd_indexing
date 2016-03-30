package de.mmenning.db.index.rtptree;

import java.util.Arrays;

import javax.xml.bind.JAXBException;

import org.xml.sax.SAXException;

import de.mmenning.db.index.NDRectangleKeyIndex;
import de.mmenning.db.index.NowGen;
import de.mmenning.db.index.evaluation.AbstractXMLIndexFactory;
import de.mmenning.db.storage.StorageManager;

public class AbstractRTPTreeFactory extends
		AbstractXMLIndexFactory<AdaptedRTPTree> {

	public AbstractRTPTreeFactory(String xmlFile) throws SAXException,
			JAXBException {
		super(xmlFile, AdaptedRTPTree.class);
	}

	@Override
	public NDRectangleKeyIndex createIndex(int dims, int blockSize,
			StorageManager s, NowGen now) {

		double[] medianMin = new double[dims];
		Arrays.fill(medianMin, this.object.getMedian().getMin());
		double[] medianMax = new double[dims];
		Arrays.fill(medianMax, this.object.getMedian().getMax());

		medianMin[dims - 2] = this.object.getMedianTT().getMin();
		medianMax[dims - 2] = this.object.getMedianTT().getMax();

		medianMin[dims - 1] = this.object.getMedianVT().getMin();
		medianMax[dims - 1] = this.object.getMedianVT().getMax();

		RTPTree tree;
		if (this.object.getNodeSizeD() == null) {
			tree = new RTPTree(dims, blockSize, s, now);
		} else {
			tree = new RTPTree(dims, this.object.getNodeSizeD(), blockSize, s,
					now);
		}

		tree.setMedian(medianMin, medianMax);
		return tree;
	}
}
