package de.mmenning.db.index.generate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import de.mmenning.db.index.NDPoint;
import de.mmenning.db.index.NDRectangle;
import de.mmenning.db.index.NDRectangleKey;
import de.mmenning.db.index.NDRectangleKeyIndex;
import de.mmenning.db.index.NowGen;
import de.mmenning.db.index.STFunctions;
import de.mmenning.db.storage.ObjectReference;
import de.mmenning.util.math.RandomInterval;

public class IndexFiller {

	public static enum DBOperation {
		UPDATE, END, START;
	}

	public static void fillIndex(NDRectangleKeyIndex i,
			ArrayList<NDRectangleKey> activeHistories,
			List<DBOperation> operations, NowGen now,
			NDRectangleGenerator spatialRecGen, RandomInterval validTime,
			Random rand) {

		for (DBOperation operation : operations) {

			NDRectangle spatial = spatialRecGen.getNextRectangle();

			double[] validTimeInterval = validTime.getNext();
			NDRectangle insert;
			NDRectangleKey update;
			NDRectangleKey old;
			int pos;

			switch (operation) {
			case START:
				insert = buildSTRectangle(spatial, now.getNow(),
						STFunctions.CURRENT, validTimeInterval[0],
						validTimeInterval[1]);
				update = new NDRectangleKey(
						ObjectReference.getReference(new Object()), insert);
				activeHistories.add(update);
				if (!i.insert(update)) {
					throw new IllegalStateException();
				}
				break;
			case UPDATE:
				/*
				 * a new version of one object
				 */
				pos = rand.nextInt(activeHistories.size());
				old = activeHistories.get(pos);
				/*
				 * end the old version
				 */
				NDRectangle oldSpatial = extractSpatialRectangle(old.getNDKey());
				NDRectangle toUpdate = buildSTRectangle(
						oldSpatial,
						old.getNDKey().getBegin()
								.getValue(old.getNDKey().getDim() - 2),
						now.getNow(),
						old.getNDKey().getBegin()
								.getValue(old.getNDKey().getDim() - 1),
						old.getNDKey().getEnd()
								.getValue(old.getNDKey().getDim() - 1));
				update = new NDRectangleKey(
						old.getObject() /* ObjectReference
								.getReference(new Object())*/,
						toUpdate);
				if (!i.update(old, update)) {
					throw new IllegalStateException(old.getNDKey()
							+ " not updated with " + update.getNDKey());
				}
				/*
				 * insert new version
				 */
				insert = buildSTRectangle(spatial, now.getNow(),
						STFunctions.CURRENT, validTimeInterval[0],
						validTimeInterval[1]);

				update = new NDRectangleKey(
						/*old.getObject()*/ ObjectReference
								.getReference(new Object()) ,
						insert);
				/*
				 * insert the new key
				 */
				if (!i.insert(update)) {
					throw new IllegalStateException();
				}
				/*
				 * set the active history of the object to the latest version
				 */
				activeHistories.set(pos, update);
				break;
			case END:
				/*
				 * end the history of an object
				 */
				pos = rand.nextInt(activeHistories.size());

				old = activeHistories.get(pos);

				oldSpatial = extractSpatialRectangle(old.getNDKey());
				toUpdate = buildSTRectangle(
						oldSpatial,
						old.getNDKey().getBegin()
								.getValue(old.getNDKey().getDim() - 2),
						now.getNow(),
						old.getNDKey().getBegin()
								.getValue(old.getNDKey().getDim() - 1),
						old.getNDKey().getEnd()
								.getValue(old.getNDKey().getDim() - 1));
				update = new NDRectangleKey(
						/* old.getObject()*/ ObjectReference
								.getReference(new Object()),
						toUpdate);

				/*
				 * delete the object from the list of active histories
				 */
				activeHistories.remove(pos);
				if (!i.update(old, update)) {
					throw new IllegalStateException();
				}
				break;
			}
			now.incNow();
		}
	}

	public static List<DBOperation> createOperations(int elements,
			double percentageStart, double percentageUpdate,
			double percentageEnd, Random rand) {

		List<DBOperation> operations = new ArrayList<DBOperation>(elements);

		for (int i = 0; i < elements * percentageStart; i++) {
			operations.add(DBOperation.START);
		}
		for (int i = 0; i < elements * percentageUpdate; i++) {
			operations.add(DBOperation.UPDATE);
		}
		for (int i = 0; i < elements * percentageEnd; i++) {
			operations.add(DBOperation.END);
		}

		Collections.shuffle(operations, rand);

		return operations;
	}

	private static NDRectangle extractSpatialRectangle(NDRectangle r) {

		double[] min = r.getBegin().toArray();
		double[] max = r.getEnd().toArray();
		double[] spatialmin = new double[r.getDim() - 2];
		double[] spatialmax = new double[r.getDim() - 2];
		System.arraycopy(min, 0, spatialmin, 0, spatialmin.length);
		System.arraycopy(max, 0, spatialmax, 0, spatialmax.length);

		return new NDRectangle(new NDPoint(spatialmin), new NDPoint(spatialmax));
	}

	public static NDRectangle buildSTRectangle(NDRectangle spatial,
			double ttBegin, double ttEnd, double vtBegin, double vtEnd) {

		double[] begin = spatial.getBegin().toArray();
		double[] end = spatial.getEnd().toArray();

		double[] newBegin = new double[begin.length + 2];
		double[] newEnd = new double[end.length + 2];

		System.arraycopy(begin, 0, newBegin, 0, begin.length);
		System.arraycopy(end, 0, newEnd, 0, end.length);

		newBegin[begin.length] = ttBegin;
		newEnd[end.length] = ttEnd;

		newBegin[begin.length + 1] = vtBegin;
		newEnd[end.length + 1] = vtEnd;

		NDRectangle rec = new NDRectangle(new NDPoint(newBegin), new NDPoint(
				newEnd));

		return rec;
	}

}
