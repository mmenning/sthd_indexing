package de.mmenning.db.storage;

public class IOUtils {

	public static int referenceByteSize() {

		String arch = System.getProperty("os.arch");

		switch (arch) {
		case "x86":
			return 4;
		case "x86_64":
		case "amd64":
			return 8;
		default:
			throw new IllegalStateException("Unknown architecture: " + arch);
		}
	}

	public static int getReferenceID(Object o) {
		return System.identityHashCode(o);
	}

	public static int bytesToBlocks(final long bytes, final int blockSize) {
		if (bytes % blockSize == 0) {
			return (int) ((double) bytes / (double) blockSize);
		} else {
			return (int) (((double) bytes / (double) blockSize) + 1.0);
		}
	}
	
}
