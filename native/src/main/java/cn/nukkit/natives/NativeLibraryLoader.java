package cn.nukkit.natives;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class NativeLibraryLoader {
  private static final LinkedHashMap<String, Path> LOADED_LIBRARIES = new LinkedHashMap<>();

  private static boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("windows");
  }

  private static boolean isOSX() {
    return System.getProperty("os.name").toLowerCase().contains("mac os");
  }

  public static boolean loadLibrary(ClassLoader loader, String libraryName) {
    if (LOADED_LIBRARIES.containsValue(Path.of(libraryName))) {
      var path = LOADED_LIBRARIES.get(libraryName);

      try {
        System.load(path.toFile().getAbsolutePath());

        return true;
      } catch (Exception exception) {
        System.err.println("Couldn't load library " + libraryName);
        exception.printStackTrace(System.err);

        return false;
      }
    }

    if (tryLoadingLibrary(libraryName)) {
      return true;
    }

    var path = extractLibrary(loader, libraryName);

    try {
      System.load(path);

      LOADED_LIBRARIES.put(libraryName, Path.of(path));

      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private static boolean tryLoadingLibrary(String libraryName) {
    try {
      System.loadLibrary(libraryName);

      return true;
    } catch (Error error) {
      return false;
    }
  }

  private static String getLibraryPrefix() {
    return isWindows() ? "" : "lib";
  }

  private static String getLibrarySuffix() {
    return isWindows() ? "dll" : isOSX() ? "dylib" : "so";
  }

  private static String extractLibrary(ClassLoader loader, String libraryName) {
    try {
      ZipEntry libraryEntry = null;
      Path tempFile;

      var jarLocation = new File(
          NativeLibraryLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI())
                            .getAbsolutePath();

      var arch = System.getProperty("os.arch");
      var path = Path.of("cn/nukkit/natives/libs", arch,
          getLibraryPrefix() + libraryName + "." + getLibrarySuffix());

      try (var zipFile = new ZipFile(jarLocation)) {
        var entries = (Iterator<? extends ZipEntry>) zipFile.entries().asIterator();

        while (entries.hasNext()) {
          var entry = entries.next();

          if (entry.isDirectory())
            continue;

          if (entry.toString().equals(path.toString())) {
            System.out.println("Found entry: " + entry);
            libraryEntry = entry;
            break;
          }
        }

        if (libraryEntry == null)
          return null;

        var stream = zipFile.getInputStream(libraryEntry);
        var bytes = stream.readAllBytes();

        tempFile = Files.createTempFile(getLibraryPrefix() + libraryName, "." + getLibrarySuffix());

        Files.write(tempFile, bytes);
      }

      return tempFile.toFile().getAbsolutePath();
    } catch (Exception exception) {
      return null;
    }
  }
}
