package org.intellij.plugins.ceylon.jps;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class ChildFirstURLClassLoader extends URLClassLoader {

    private final String[] systemPathsToAvoid;
    private final List<String> classesToSkip;
    private ClassLoader system;

    public ChildFirstURLClassLoader(URL[] classpath, ClassLoader parent, String[] systemPathsToAvoid, String[] classesToSkip) {
        super(classpath, parent);
        this.systemPathsToAvoid = systemPathsToAvoid;
        this.classesToSkip = Arrays.asList(classesToSkip);
        system = getSystemClassLoader();
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException {

        if (classesToSkip.contains(name)) {
            return null;
        }

        // First, check if the class has already been loaded
        Class<?> c = findLoadedClass(name);
        if (c == null) {
            if (canUseSystem(name, false) && system != null) {
                try {
                    // checking system: jvm classes, endorsed, cmd classpath, etc.
                    c = system.loadClass(name);
                } catch (ClassNotFoundException ignored) {
                }
            }
            if (c == null) {
                try {
                    // checking local
                    c = findClass(name);
                } catch (ClassNotFoundException e) {
                    // checking parent
                    // This call to loadClass may eventually call findClass again, in case the parent doesn't find anything.
                    c = super.loadClass(name, resolve);
                }
            }
        }
        if (resolve) {
            resolveClass(c);
        }
        return c;
    }

    private boolean canUseSystem(String name, boolean isPath) {
        if (systemPathsToAvoid.length == 0) {
            return true;
        }

        for (String packageToSkip : systemPathsToAvoid) {
            if (isPath) {
                packageToSkip = packageToSkip.replace('.', '/');
            }
            if (name.startsWith(packageToSkip)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public URL getResource(String name) {
        URL url = null;
        if (canUseSystem(name, true) && system != null) {
            url = system.getResource(name); 
        }
        if (url == null) {
            url = findResource(name);
            if (url == null) {
                // This call to getResource may eventually call findResource again, in case the parent doesn't find anything.
                url = super.getResource(name);
            }
        }
        return url;
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        /**
        * Similar to super, but local resources are enumerated before parent resources
        */
        Enumeration<URL> systemUrls = null;
        if (canUseSystem(name, true) && system != null) {
            systemUrls = system.getResources(name);
        }
        Enumeration<URL> localUrls = findResources(name);
        Enumeration<URL> parentUrls = null;
        if (getParent() != null) {
            parentUrls = getParent().getResources(name);
        }
        final List<URL> urls = new ArrayList<URL>();
        if (systemUrls != null) {
            while(systemUrls.hasMoreElements()) {
                urls.add(systemUrls.nextElement());
            }
        }
        if (localUrls != null) {
            while (localUrls.hasMoreElements()) {
                urls.add(localUrls.nextElement());
            }
        }
        if (parentUrls != null) {
            while (parentUrls.hasMoreElements()) {
                urls.add(parentUrls.nextElement());
            }
        }
        return new Enumeration<URL>() {
            Iterator<URL> iter = urls.iterator();

            public boolean hasMoreElements() {
                return iter.hasNext(); 
            }
            public URL nextElement() {
                return iter.next();
            }
        };
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        URL url = getResource(name);
        try {
            return url != null ? url.openStream() : null;
        } catch (IOException e) {
        }
        return null;
    }

}