package components;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * list resources available from the classpath @ *
 */
public class FindResources {

    public static final Logger LOGGER = Logger.getLogger(FindResources.class.getName());

    /**
     * for all elements of java.class.path get a Collection of resources Pattern
     * pattern = Pattern.compile(".*"); gets all resources
     *
     * @param pattern
     *            the pattern to match
     * @return the resources in the order they are found
     */
    public static List<String> getResources(final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();
        final String classPath = System.getProperty("java.class.path", ".");
        final String[] classPathElements = classPath.split(File.pathSeparator);
        for (final String element : classPathElements) {
            retval.addAll(getResources(element, pattern));
        }
        return retval;
    }

    private static Collection<String> getResources(final String element, final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();
        final File file = new File(element);
        if (!file.exists()) {
            return retval;
        }
        if (file.isDirectory()) {
            try {
                retval.addAll(getResourcesFromDirectory(file.getCanonicalFile(), pattern, file.getCanonicalPath()));
            } catch (IOException e) {
            }
        } else {
            retval.addAll(getResourcesFromJarFile(file, pattern));
        }
        return retval;
    }

    private static Collection<String> getResourcesFromJarFile(final File file, final Pattern pattern) {
        final ArrayList<String> retval = new ArrayList<String>();
        ZipFile zf;
        try {
            zf = new ZipFile(file);
        } catch (final ZipException e) {
            throw new Error(e);
        } catch (final IOException e) {
            throw new Error(e);
        }
        final Enumeration<?> e = zf.entries();
        while (e.hasMoreElements()) {
            final ZipEntry ze = (ZipEntry) e.nextElement();
            final String fileName = ze.getName();
            final boolean accept = pattern.matcher(fileName).matches();
            if (accept) {
                retval.add(fileName);
            }
        }
        try {
            zf.close();
        } catch (final IOException e1) {
            throw new Error(e1);
        }
        return retval;
    }

    private static Collection<String> getResourcesFromDirectory(final File directory, final Pattern pattern, String dirPath) {
        final ArrayList<String> retval = new ArrayList<String>();
        final File[] fileList = directory.listFiles();
        if (fileList != null) {
            for (final File file : fileList) {
                if (file.isDirectory()) {
                    retval.addAll(getResourcesFromDirectory(file, pattern, dirPath));
                } else {
                    try {
                        String fileName = file.getCanonicalPath();
                        final boolean accept = pattern.matcher(fileName).matches();
                        if (accept) {
                            if (fileName.startsWith(dirPath)) {
                                fileName = fileName.substring((int) dirPath.length() + 1);
                            }
                            retval.add(fileName);
                        }
                    } catch (final IOException e) {
                        throw new Error(e);
                    }
                }
            }
        }
        return retval;
    }

    public static List<String> findClasses(final String klassName) {
        final Pattern pattern = Pattern.compile(".*" + klassName + "\\.class$");
        final Collection<String> list = FindResources.getResources(pattern);
        ArrayList<String> classes = new ArrayList<String>();
        for (final String name : list) {
            classes.add(name.replace('/', '.').replace('\\', '.').substring(0, name.length() - 6));
        }
        return classes;
    }

    /**
     * list the resources that match args[0]
     *
     * @param args
     *            args[0] is the pattern to match, or list all resources if
     *            there are no args
     */
    public static void main(final String[] args) {
        final String klassName = args.length > 1 ? args[0] : "Player";
        final Collection<String> classes = findClasses(klassName);
        Logger.getLogger(FindResources.class.getName()).info(classes.toString());
    }

}
