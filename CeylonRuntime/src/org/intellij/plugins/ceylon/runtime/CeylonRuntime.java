package org.intellij.plugins.ceylon.runtime;

import com.intellij.ide.plugins.cl.PluginClassLoader;
import com.redhat.ceylon.cmr.api.ArtifactResult;
import com.redhat.ceylon.compiler.java.runtime.metamodel.Metamodel;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by david on 19/01/15.
 */
public class CeylonRuntime extends PluginCeylonStartup {
    static Map<String, String> registeredModules = new HashMap<>();

    public void disposeComponent() {
        Metamodel.resetModuleManager();
        registeredModules.clear();
    }

    static void registerModule(ArtifactResult moduleArtifact, PluginClassLoader classLoader) {
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
                    ! alreadyRegisteredModuleMD5.equals(artifactMD5)) {
            throw new RuntimeException("Ceylon Metamodel Registering failed : the module '" +
                    moduleArtifact.name() + "/" + moduleArtifact.version() +
                    "' of plugin '" + classLoader.getPluginId().toString() + "' cannot be registered since it has already been registered " +
                    "by another plugin with a different binary archive");
        } else {
            // then don't need to register again
        }
    }

    public static String generateBufferedHash(File file)
            throws NoSuchAlgorithmException,
            FileNotFoundException, IOException {

        MessageDigest md = MessageDigest.getInstance("MD5");

        InputStream is= new FileInputStream(file);

        byte[] buffer=new byte[8192];
        int read=0;

        while( (read = is.read(buffer)) > 0)
            md.update(buffer, 0, read);

        byte[] md5 = md.digest();
        BigInteger bi=new BigInteger(1, md5);

        return bi.toString(16);
    }

    @NotNull
    public String getComponentName() {
        return "CeylonRuntime";
    }
}
