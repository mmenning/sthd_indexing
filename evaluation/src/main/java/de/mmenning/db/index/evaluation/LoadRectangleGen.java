package de.mmenning.db.index.evaluation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import de.mmenning.db.index.generate.NDRandomRectangleGenerator;
import de.mmenning.db.index.generate.NDRectangleGenerator;

public class LoadRectangleGen {
	
	public static NDRectangleGenerator loadRectangleGen(final File file,
			NDRectangleGenerator defaultGen) {

		NDRectangleGenerator gen = null;

		if (!file.exists()) {
			try (ObjectOutputStream oOut = new ObjectOutputStream(
					new FileOutputStream(file));) {
				oOut.writeObject(defaultGen);
				gen = defaultGen;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try (ObjectInputStream oIn = new ObjectInputStream(
					new FileInputStream(file));) {
				gen = (NDRandomRectangleGenerator) oIn.readObject();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		return gen;

	}

}
