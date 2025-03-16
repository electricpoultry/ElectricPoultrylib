package io.github.coolman4567.xboxmanlib.util;

public class Basic {
    public static String prefixNamespace(ModResourceLocation registryKey) {
        return registryKey.getNamespace().equals("minecraft") ? registryKey.getPath() : registryKey.getNamespace() + "/" + registryKey.getPath();
    }
}
