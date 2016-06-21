package com.ekkongames.elib.io;

import controlP5.*;

public class Console {

  private static Textarea out;
  private static String lastOutput = "";

  public static void init(Textarea out) {
	Console.out = out;
  }
  
  public static String getLastOutput() {
    return lastOutput;
  }

  public static void outln(Object o) {
    out(o + System.lineSeparator());
  }

  public static void errln(Object o) {
    err(o + System.lineSeparator());
  }

  public static void outf(String format, Object... args) {
    out(String.format(format, args));
  }

  public static void errf(String format, Object... args) {
    err(String.format(format, args));
  }

  public static void out(Object o) {
    lastOutput = o.toString();
    System.out.print(lastOutput);

    if (out != null) {
      out.append(lastOutput);
      out.scroll(1);
    }
  }

  public static void err(Object o) {
    lastOutput = "[ERROR] " + o.toString();
    System.err.print(lastOutput);

    if (out != null) {
      out.append(lastOutput);
      out.scroll(1);
    }
  }
}