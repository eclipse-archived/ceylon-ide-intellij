package org.intellij.plugins.ceylon.runtime;

import com.intellij.ide.plugins.cl.PluginClassLoader;
import com.intellij.openapi.application.ApplicationInfo;
import com.redhat.ceylon.compiler.java.runtime.metamodel.Metamodel;
import com.redhat.ceylon.model.cmr.*;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CeylonRuntime extends PluginCeylonStartup {
    static Map<String, String> registeredModules = new HashMap<>();

    public static void registerIntellijApiModules() {
        ApplicationInfo applicationInfo = ApplicationInfo.getInstance();
        ClassLoader cl = applicationInfo.getClass().getClassLoader();
        final String version = applicationInfo.getMajorVersion();
        Class<? extends ClassLoader> loaderClass = cl.getClass();

        List<URL> urls = Collections.emptyList();
        try {
            Method method = loaderClass.getMethod("getUrls");
            Object result = method.invoke(cl);
            if (result instanceof List) {
                urls = (List<URL>) result;
            }
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }

        for (final URL url : urls) {
            final String fileName = url.getFile();
            if (fileName.endsWith("openapi.jar") ||
                    fileName.endsWith("util.jar") ||
                    fileName.endsWith("annotations.jar") ||
                    fileName.endsWith("extensions.jar")) {
                ArtifactResult artifactResult = new ArtifactResult() {
                    @Override
                    public String name() {
                        return fileName.replaceAll("\\.jar$", "");
                    }

                    @Override
                    public String version() {
                        return version;
                    }

                    @Override
                    public ImportType importType() {
                        return ImportType.EXPORT;
                    }

                    @Override
                    public ArtifactResultType type() {
                        return ArtifactResultType.CEYLON;
                    }

                    @Override
                    public VisibilityType visibilityType() {
                        return VisibilityType.STRICT;
                    }

                    @Override
                    public File artifact() throws RepositoryException {
                        try {
                            return new File(url.toURI());
                        } catch (URISyntaxException e) {
                            throw new RepositoryException(e);
                        }
                    }

                    @Override
                    public PathFilter filter() {
                        return null;
                    }

                    @Override
                    public List<ArtifactResult> dependencies() throws RepositoryException {
                        return Collections.emptyList();
                    }

                    @Override
                    public String repositoryDisplayString() {
                        return null;
                    }

                    @Override
                    public Repository repository() {
                        return null;
                    }
                };
                registerModule(artifactResult, cl);
            }
        }
    }

    public void disposeComponent() {
        Metamodel.resetModuleManager();
        registeredModules.clear();
    }

    static public void registerModule(ArtifactResult moduleArtifact, ClassLoader classLoader) {
        String artifactFileName = moduleArtifact.artifact().getName();
        String artifactMD5 = "";
        try {
            artifactMD5 = generateBufferedHash(moduleArtifact.artifact());
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }

        String alreadyRegisteredModuleMD5 = registeredModules.get(artifactFileName);
        if (alreadyRegisteredModuleMD5 == null) {
            Metamodel.loadModule(moduleArtifact.name(), moduleArtifact.version(), moduleArtifact, classLoader);
            registeredModules.put(artifactFileName, artifactMD5);
        } else if (alreadyRegisteredModuleMD5.isEmpty() ||
                !alreadyRegisteredModuleMD5.equals(artifactMD5)) {
            String originMessage = "";
            if (classLoader instanceof PluginClassLoader) {
                originMessage = " of plugin '" + ((PluginClassLoader) classLoader).getPluginId().toString() + "'";
            }
            throw new RuntimeException("Ceylon Metamodel Registering failed : the module '" +
                    moduleArtifact.name() + "/" + moduleArtifact.version() +
                    "'" + originMessage + " cannot be registered since it has already been registered " +
                    "by another plugin with a different binary archive");
        } else {
            // then don't need to register again
        }
    }

    public static String generateBufferedHash(File file)
            throws NoSuchAlgorithmException,
            FileNotFoundException, IOException {

        MessageDigest md = MessageDigest.getInstance("MD5");

        InputStream is = new FileInputStream(file);

        byte[] buffer = new byte[8192];
        int read = 0;

        while ((read = is.read(buffer)) > 0)
            md.update(buffer, 0, read);

        byte[] md5 = md.digest();
        BigInteger bi = new BigInteger(1, md5);

        return bi.toString(16);
    }

    @NotNull
    public String getComponentName() {
        return "CeylonRuntime";
    }
}
