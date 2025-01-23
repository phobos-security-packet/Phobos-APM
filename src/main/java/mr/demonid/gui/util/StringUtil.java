package mr.demonid.gui.util;

import java.io.File;
import java.net.URL;

public class StringUtil {

    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static URL getResourceURL(String name)
    {
        try {
            URL url = Class.forName("mr.demonid.gui.App").getResource("/resources/" + name);
            return url == null ? getFilePath(name): url;
        } catch (Exception e) {
            return getFilePath(name);
        }
    }

    private static URL getFilePath(String name)
    {
        try {
            URL url = new File("/resources/resources/" + name).toURI().toURL();
            System.out.println(url);
            return url;
        } catch (Exception ignored) {
            return null;
        }
    }

}
