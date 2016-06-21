package com.ekkongames.elib.io;

import java.util.Enumeration;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Observable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class CompressedFile  extends Observable implements Runnable {

  // the number of bytes that have been extracted
  private volatile int extracted;

  // the total number of bytes to extract
  private volatile int total;

  // These are the status names.
  public static final String STATUSES[] = {"Extracting", 
    "Paused", "Complete", "Cancelled", "Error", ""};

  // These are the status codes.
  public static final int EXTRACTING = 0;
  public static final int PAUSED = 1;
  public static final int COMPLETE = 2;
  public static final int CANCELLED = 3;
  public static final int ERROR = 4;
  public static final int NONE = 5;

  // the source file
  private final File src;
  private File destDIR;
  private int status;

  public CompressedFile(File src) {
    this.src = src;

    extracted = 0;
    total = 0;
    status = NONE;
  }

  public float getProgress() {
    if (total == 0) {
      return 0.0F;
    }

    return ((extracted / (1.0F * total)) * 100) + 0.0F;
  }

  // Get this download's status.
  public int getStatus() {
    return status;
  }

  public void cancel() {
    status = CANCELLED;
    stateChanged();

    if (extracted != total) {
      Console.errln("Some files may be corrupted!");
    }
  }

  private void error() {
    status = ERROR;
    stateChanged();
  }

  public void extract(File destDIR) {
    this.destDIR = destDIR;
    status = EXTRACTING;
    stateChanged();

    Thread thread = new Thread(this);
    thread.start();
  }

  public void run() {
    byte[] buff = new byte[1024];
    ZipFile zipFile = null;
    File containingFolder = null;
    int baseFileCount = 0;
    try {
      // this is where you start, with an InputStream containing the bytes from the zip file
      zipFile = new ZipFile(src);
      ZipEntry entry;
      total = 0;
      extracted = 0;
      for (Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements(); ) {
        ZipEntry ze = entries.nextElement();
        File path = new File(destDIR, ze.getName());

        if (path.getParentFile().equals(src)) {
          baseFileCount++;
        }

        if (containingFolder == null && ze.isDirectory()) {
          containingFolder = FileUtils.getFirstSubdirectory(destDIR, path);
        }
        long filesize = ze.getSize();
        if (filesize > 0) {
          total += filesize;
        }
      }

      if (baseFileCount > 1) {
        containingFolder = null;
      } else if (containingFolder != null) {
        Console.outln("Containing folder! " + containingFolder);
      }

      // while there are entries, process them
      for (Enumeration<? extends ZipEntry> entries = zipFile.entries(); entries.hasMoreElements(); ) {
        entry = (ZipEntry) entries.nextElement();
        // consume all the data from this entry
        InputStream ein = null;
        FileOutputStream fout = null;
        File output = new File(destDIR, entry.getName());
        File folder = output.getParentFile();
        if (containingFolder != null) {
          folder = new File(destDIR, folder.getAbsolutePath().replace(containingFolder.getAbsolutePath(), ""));
          output = new File(folder, output.getName());
        }
        Console.outln("Extracting " + entry.getName().replace(containingFolder.getName(), ""));

        folder.mkdirs();
        if (!entry.isDirectory()) {
          output.createNewFile();
          try {
            ein = zipFile.getInputStream(entry);
            fout = new FileOutputStream(output);
            int size = 0;
            // write buffer to file
            while ((size = ein.read(buff)) != -1) {
              fout.write(buff, 0, size);
              extracted += size;

              if (status != EXTRACTING) return;
            }
          }
          catch (Exception e) {
            Console.errf("Exception writing \"%s\" to disk (%s). See the Processing console for details.%n", output, e);
            e.printStackTrace();
          } 
          finally {
            FileUtils.close(ein, fout);
          }
        }
      }
    }
    catch (Exception e) {
      Console.errf("Exception caught within Unzip Class (%s). See the Processing console for details.%n", e);
      e.printStackTrace();
    } 
    finally {
      FileUtils.close(zipFile);
      Console.outln("Extraction complete!");
    }
  }

  // Notify observers that this download's status has changed.
  private void stateChanged() {
    setChanged();
    notifyObservers();
  }
}