package com.ekkongames.elib.io;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.io.Flushable;
import java.io.Closeable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipFile;

public class FileUtils {

  public static void copy(File src, File dest) {
    try {
      Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
    } 
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static File[] getRealBase(Path base) {
    File[] baseChildren = base.toFile().listFiles();

    if (baseChildren.length == 1) return getRealBase(baseChildren[0].toPath());
    return baseChildren;
  }

  public static File getFirstSubdirectory(File base, File path) {
    String fs = base.toPath().relativize(Paths.get(path.toURI())).toString();
    int endIndex = fs.indexOf(File.separator);
    return new File(base, endIndex == -1 ? fs : fs.substring(0, endIndex));
  }

  public static void close(Object... toClose) {
    for (Object closeMe : toClose) {
      if (closeMe != null) {
        try {
          if (closeMe instanceof Flushable) {
            ((Flushable) closeMe).flush();
          }
          if (closeMe instanceof Closeable) {
            ((Closeable) closeMe).close();
          }
          if (closeMe instanceof ZipFile) {
            ((ZipFile) closeMe).close();
          }
        } 
        catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}