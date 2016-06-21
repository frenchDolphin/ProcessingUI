package com.ekkongames.elib.io;

import java.io.File;
import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.util.Observable;
import javax.net.ssl.HttpsURLConnection;

// This class downloads a file from a URL.
public class Download extends Observable implements Runnable {

  // Max size of download buffer.
  private static final int MAX_BUFFER_SIZE = 1024;

  // These are the status names.
  public static final String STATUSES[] = {"Downloading", 
    "Paused", "Complete", "Cancelled", "Error"};

  // These are the status codes.
  public static final int DOWNLOADING = 0;
  public static final int PAUSED = 1;
  public static final int COMPLETE = 2;
  public static final int CANCELLED = 3;
  public static final int ERROR = 4;

  private URL url; // download URL
  private File tempFile;
  private volatile int size; // size of download in bytes
  private volatile int downloaded; // number of bytes downloaded
  private volatile int status; // current status of download

  // Constructors

  public Download(String src) throws MalformedURLException, IOException {
    this(new URL(src));
  }

  public Download(URL url) throws IOException {
    this.url = url;
    tempFile = File.createTempFile("download", ".pdl");
    tempFile.deleteOnExit();
    size = -1;
    downloaded = 0;
    status = DOWNLOADING;

    // Begin the download.
    Console.outln("Starting download from " + url);
    download();
  }

  public File getOutput() {
    if (status == COMPLETE) {
      return tempFile;
    }
    return null;
  }

  // Get this download's URL.
  public String getURL() {
    return url.toString();
  }

  // Get this download's size.
  public int getSize() {
    return size;
  }

  // Get this download's progress.
  public float getProgress() {
    // adding 0.0F fixes a bug where a float with a value of 0.0 prints "-0.0"
    return ((downloaded / (1.0F * size)) * 100) + 0.0F;
  }

  // Get this download's status.
  public int getStatus() {
    return status;
  }

  // Pause this download.
  @Deprecated
  public void pause() {
    System.out.printf("Pausing download... (%d/%d bytes downloaded)%n", downloaded, size);
    status = PAUSED;
    stateChanged();
  }

  // Resume this download.
  @Deprecated
  public void resume() {
    System.out.println("Resuming download...");
    status = DOWNLOADING;
    stateChanged();
    download();
  }

  // Cancel this download.
  public void cancel() {
    status = CANCELLED;
    stateChanged();
  }

  // Mark this download as having an error.
  private void error() {
    status = ERROR;
    stateChanged();
  }

  // Start or resume downloading.
  private void download() {
    Thread thread = new Thread(this);
    thread.start();
  }

  // Get file name portion of URL.
  private String getFileName(URL url) {
    String fileName = url.getFile();
    return fileName.substring(fileName.lastIndexOf('/') + 1);
  }

  // Download file.
  public void run() {
    BufferedInputStream in = null;
    BufferedOutputStream out = null, fileOut = null;
    boolean setComplete = false;

    try {
      URL finalURL = getFinalURL(url);

      // Open connection to URL.
      HttpURLConnection connection =
        (HttpURLConnection) finalURL.openConnection();

      // Specify what portion of file to download.
      connection.addRequestProperty("Range", 
        "bytes=" + downloaded + "-");
      connection.setInstanceFollowRedirects(true);

      // Connect to server.
      connection.connect();

      if (connection.getResponseCode() / 100 != 2) {
        System.err.println("Bad HTTP response code: " + connection.getResponseCode());
        error();
        return;
      }

      // Check for valid content length.
      int contentLength = connection.getContentLength();
      if (contentLength < 1) {
        System.err.println("Zero or negative content length!");
        error();
        return;
      }

      /* Set the size for this download if it
       hasn't been already set. */
      if (size == -1) {
        size = contentLength;
        stateChanged();
      }

      in = new BufferedInputStream(connection.getInputStream());
      out = new BufferedOutputStream(new FileOutputStream(tempFile), 1024);

      while (status == DOWNLOADING) {
        /* Size buffer according to how much of the
         file is left to download. */
        byte[] buffer;
        if (size - downloaded > MAX_BUFFER_SIZE) {
          buffer = new byte[MAX_BUFFER_SIZE];
        } else {
          buffer = new byte[size - downloaded];
        }

        // Read from server into buffer.
        int read = in.read(buffer);
        if (read == -1 || size == downloaded) {
          Console.outln("File downloaded!");
          break;
        }

        // Write buffer to file.
        out.write(buffer, 0, read);
        downloaded += read;
        stateChanged();
      }

      /* Change status to complete if this point was
       reached because downloading has finished. */
      if (status == DOWNLOADING) {
        Console.outln("Succesfully downloaded " + url + " (" + downloaded + " bytes).");
        setComplete = true;
        stateChanged();
      }
    } 
    catch (Exception e) {
      e.printStackTrace();
      error();
    } 
    finally {
      System.out.println("Closing any open streams...");
      FileUtils.close(in, out, fileOut);
      if (setComplete) {
        status = COMPLETE;
      }
    }
  }

  private URL getFinalURL(URL url) throws IOException {
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setInstanceFollowRedirects(false);
    con.connect();
    con.getInputStream();

    if (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
      String redirectUrl = con.getHeaderField("Location");
      return getFinalURL(new URL(redirectUrl));
    }
    return url;
  }

  // Notify observers that this download's status has changed.
  private void stateChanged() {
    setChanged();
    notifyObservers();
  }
}