package de.mmenning.db.storage;

import java.io.*;
import java.util.Iterator;

public class FileStorage implements StorageManager {

   private static final String dbdir = "db";
   private final static String dbpraefix = dbdir + "//db";

   public FileStorage() {
      File dir = new File(dbdir);

      if (!dir.exists()) {
         dir.mkdirs();
      }
   }

   @Override
   public void cleanUp() {
      File dir = new File(dbdir);

      for (File f : dir.listFiles()) {
         f.delete();
      }
   }

   @Override
   public Storable load(ObjectReference or) {

      File f = getFile(or);
      if (!f.exists()) {
         throw new StorageIOException(or + " does not exist");
      }

      try (FileInputStream fw = new FileInputStream(f);
           ObjectInputStream o = new ObjectInputStream(fw)) {

         Storable st = new SimpleStorable(or, o.readObject(), o.available());

         return st;

      } catch (IOException | ClassNotFoundException e) {
         throw new StorageIOException(e);
      }
   }

   @Override
   public void delete(ObjectReference or) {
      File f = getFile(or);
      if (!f.exists() || !f.delete()) {
         throw new StorageIOException(or + "not deleted!");
      }
   }

   private static File getFile(ObjectReference or) {
      return new File(dbpraefix + "#" + or.getID() + ".ser");
   }

   @Override
   public void store(Storable st) {

      File f = getFile(st.getObjectReference());
      if (!f.exists()) {
         try {
            f.createNewFile();
         } catch (IOException e) {
            throw new StorageIOException(e);
         }
      }

      try (FileOutputStream fw = new FileOutputStream(f);
           ObjectOutputStream o = new ObjectOutputStream(fw)) {

         o.writeObject(st.getBytes());

      } catch (IOException e) {
         throw new StorageIOException(e);
      }
   }

   class StorageIOException extends IllegalStateException {

      private static final long serialVersionUID = 3179691377654359305L;

      public StorageIOException(Exception e) {
         super(e);
      }

      public StorageIOException(String string) {
         super(string);
      }
   }
}
