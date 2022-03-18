package ooo.paulsen.utils;

import java.util.Locale;

public class PSystem {

    public enum OSType {
        WINDOWS, MACOS, LINUX, SOLARIS, OTHER
    }

    public static OSType getOSType() {
        String osName = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);

        if (osName.contains("mac") || osName.contains("darwin")) {
            return OSType.MACOS;
        } else if (osName.contains("win")) {
            return OSType.WINDOWS;
        } else if (osName.contains("nix") || osName.contains("nux")
                || osName.contains("aix")) {
            return OSType.LINUX;
        } else if (osName.contains("sunos")) {
            return OSType.SOLARIS;
        }

        return OSType.OTHER;
    }

    private PSystem() {
    }

    /**
     * Character that separates components of a file path.
     *
     * @return '/' on UNIX-based and '\' on Windows-based Systems
     */
    public static char getFileSeparator() {
        return System.getProperty("file.separator").charAt(0);
    }

    /**
     * @return username
     */
    public static String getUserName() {
        return System.getProperty("user.name");
    }

    /**
     * Uses os-specific folder-separators in path-string
     *
     * @return Path of the Folder, where the java-program is run from
     */
    public static String getWorkingDirectory() {
        return System.getProperty("user.dir");
    }

    /**
     * @return encoding of files (e.g. UTF-8)
     */
    public static String getEncoding() {
        return System.getProperty("file.encoding");
    }

    /**
     * @return "\r\n" for windows or "\n" for Unix/Mac OS X
     */
    public static String getLineSeparators() {
        return System.getProperty("line.separator");
    }

    /**
     * @return The Language-Name of the current system spelled <b>in the language</b> of the system
     */
    public static String getUserDisplayLanguage() {
        return Locale.getDefault().getDisplayLanguage();
    }

    /**
     * @return The Country-Name of the current system spelled <b>in the language</b> of the system
     */
    public static String getUserDisplayLocation() {
        return Locale.getDefault().getDisplayCountry();
    }


}
