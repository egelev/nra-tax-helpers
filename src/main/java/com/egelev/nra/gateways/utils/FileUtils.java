package com.egelev.nra.gateways.utils;


import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class FileUtils {

  private FileUtils() {}

  public static Set<File> collectFilesRecursively(File start) {
    if (!start.exists()) {
      return Collections.emptySet();
    }

    if (start.isFile()) {
      return Set.of(start);
    }

    Set<File> result = new HashSet<>();
    for (File f : start.listFiles()) {
      if (f.isFile()) {
        result.add(f);
      } else if (f.isDirectory()){
        result.addAll(collectFilesRecursively(f));
      }
    }

    return result;
  }

}
