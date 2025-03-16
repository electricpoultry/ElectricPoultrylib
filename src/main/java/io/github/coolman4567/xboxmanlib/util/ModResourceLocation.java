package io.github.coolman4567.xboxmanlib.util;

import com.google.gson.*;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.function.UnaryOperator;

public class ModResourceLocation implements Comparable<ModResourceLocation> {
    public static final Codec<ModResourceLocation> CODEC = Codec.STRING
            .<ModResourceLocation>comapFlatMap(ModResourceLocation::read, ModResourceLocation::toString)
            .stable();
    public static final StreamCodec<ByteBuf, ModResourceLocation> STREAM_CODEC = ByteBufCodecs.STRING_UTF8
            .map(ModResourceLocation::parse, ModResourceLocation::toString);
    public static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(Component.translatable("argument.id.invalid"));
    public static final char NAMESPACE_SEPARATOR = ':';
    public static final String DEFAULT_NAMESPACE = "minecraft";
    public static final String REALMS_NAMESPACE = "realms";
    private final String namespace;
    private final String path;

    public ModResourceLocation(String namespace, String path) {
        assert isValidNamespace(namespace);

        assert isValidPath(path);

        this.namespace = namespace;
        this.path = path;
    }

    private static ModResourceLocation createUntrusted(String namespace, String path) {
        return new ModResourceLocation(assertValidNamespace(namespace, path), assertValidPath(namespace, path));
    }

    public static ModResourceLocation fromNamespaceAndPath(String namespace, String path) {
        return createUntrusted(namespace, path);
    }

    public static ModResourceLocation parse(String location) {
        return bySeparator(location, ':');
    }

    public static ModResourceLocation withDefaultNamespace(String location) {
        return new ModResourceLocation("minecraft", assertValidPath("minecraft", location));
    }

    @Nullable
    public static ModResourceLocation tryParse(String location) {
        return tryBySeparator(location, ':');
    }

    @Nullable
    public static ModResourceLocation tryBuild(String namespace, String path) {
        return isValidNamespace(namespace) && isValidPath(path) ? new ModResourceLocation(namespace, path) : null;
    }

    public static ModResourceLocation bySeparator(String location, char seperator) {
        int i = location.indexOf(seperator);
        if (i >= 0) {
            String s = location.substring(i + 1);
            if (i != 0) {
                String s1 = location.substring(0, i);
                return createUntrusted(s1, s);
            } else {
                return withDefaultNamespace(s);
            }
        } else {
            return withDefaultNamespace(location);
        }
    }

    @Nullable
    public static ModResourceLocation tryBySeparator(String location, char seperator) {
        int i = location.indexOf(seperator);
        if (i >= 0) {
            String s = location.substring(i + 1);
            if (!isValidPath(s)) {
                return null;
            } else if (i != 0) {
                String s1 = location.substring(0, i);
                return isValidNamespace(s1) ? new ModResourceLocation(s1, s) : null;
            } else {
                return new ModResourceLocation("minecraft", s);
            }
        } else {
            return isValidPath(location) ? new ModResourceLocation("minecraft", location) : null;
        }
    }

    public static DataResult<ModResourceLocation> read(String location) {
        try {
            return DataResult.success(parse(location));
        } catch (ResourceLocationException resourcelocationexception) {
            return DataResult.error(() -> "Not a valid resource location: " + location + " " + resourcelocationexception.getMessage());
        }
    }

    public String getPath() {
        return this.path;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public ModResourceLocation withPath(String path) {
        return new ModResourceLocation(this.namespace, assertValidPath(this.namespace, path));
    }

    public ModResourceLocation withPath(UnaryOperator<String> pathOperator) {
        return this.withPath(pathOperator.apply(this.path));
    }

    public ModResourceLocation withPrefix(String pathPrefix) {
        return this.withPath(pathPrefix + this.path);
    }

    public ModResourceLocation withSuffix(String pathSuffix) {
        return this.withPath(this.path + pathSuffix);
    }

    @Override
    public String toString() {
        return this.namespace + ":" + this.path;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else {
            return !(other instanceof ModResourceLocation resourcelocation)
                    ? false
                    : this.namespace.equals(resourcelocation.namespace) && this.path.equals(resourcelocation.path);
        }
    }

    @Override
    public int hashCode() {
        return 31 * this.namespace.hashCode() + this.path.hashCode();
    }

    @Override
    public int compareTo(@NotNull ModResourceLocation other) {
        int i = this.path.compareTo(other.path);
        if (i == 0) {
            i = this.namespace.compareTo(other.namespace);
        }

        return i;
    }

    // Normal compare sorts by path first, this compares namespace first.
    public int compareNamespaced(ModResourceLocation o) {
        int ret = this.namespace.compareTo(o.namespace);
        return ret != 0 ? ret : this.path.compareTo(o.path);
    }

    public String toDebugFileName() {
        return this.toString().replace('/', '_').replace(':', '_');
    }

    public String toLanguageKey() {
        return this.namespace + "." + this.path;
    }

    public String toShortLanguageKey() {
        return this.namespace.equals("minecraft") ? this.path : this.toLanguageKey();
    }

    public String toLanguageKey(String type) {
        return type + "." + this.toLanguageKey();
    }

    public String toLanguageKey(String type, String key) {
        return type + "." + this.toLanguageKey() + "." + key;
    }

    private static String readGreedy(StringReader reader) {
        int i = reader.getCursor();

        while (reader.canRead() && isAllowedInResourceLocation(reader.peek())) {
            reader.skip();
        }

        return reader.getString().substring(i, reader.getCursor());
    }

    public static ModResourceLocation read(StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        String s = readGreedy(reader);

        try {
            return parse(s);
        } catch (ResourceLocationException resourcelocationexception) {
            reader.setCursor(i);
            throw ERROR_INVALID.createWithContext(reader);
        }
    }

    public static ModResourceLocation readNonEmpty(StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        String s = readGreedy(reader);
        if (s.isEmpty()) {
            throw ERROR_INVALID.createWithContext(reader);
        } else {
            try {
                return parse(s);
            } catch (ResourceLocationException resourcelocationexception) {
                reader.setCursor(i);
                throw ERROR_INVALID.createWithContext(reader);
            }
        }
    }

    public static boolean isAllowedInResourceLocation(char character) {
        return character >= '0' && character <= '9'
                || character >= 'a' && character <= 'z'
                || character == '_'
                || character == ':'
                || character == '/'
                || character == '.'
                || character == '-';
    }

    /**
     * @return {@code true} if the specified {@code path} is valid: consists only of {@code [a-z0-9/._-]} characters
     */
    public static boolean isValidPath(String path) {
        for (int i = 0; i < path.length(); i++) {
            if (!validPathChar(path.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * @return {@code true} if the specified {@code namespace} is valid: consists only of {@code [a-z0-9_.-]} characters
     */
    public static boolean isValidNamespace(String namespace) {
        for (int i = 0; i < namespace.length(); i++) {
            if (!validNamespaceChar(namespace.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    private static String assertValidNamespace(String namespace, String path) {
        if (!isValidNamespace(namespace)) {
            throw new ResourceLocationException("Non [a-z0-9_.-] character in namespace of location: " + namespace + ":" + path);
        } else {
            return namespace;
        }
    }

    public static boolean validPathChar(char pathChar) {
        return pathChar == '_'
                || pathChar == '-'
                || pathChar >= 'a' && pathChar <= 'z'
                || pathChar >= '0' && pathChar <= '9'
                || pathChar == '/'
                || pathChar == '.';
    }

    public static boolean validNamespaceChar(char namespaceChar) {
        return namespaceChar == '_' || namespaceChar == '-' || namespaceChar >= 'a' && namespaceChar <= 'z' || namespaceChar >= '0' && namespaceChar <= '9' || namespaceChar == '.';
    }

    private static String assertValidPath(String namespace, String path) {
        if (!isValidPath(path)) {
            throw new ResourceLocationException("Non [a-z0-9/._-] character in path of location: " + namespace + ":" + path);
        } else {
            return path;
        }
    }

    public static class Serializer implements JsonDeserializer<ModResourceLocation>, JsonSerializer<ModResourceLocation> {
        public ModResourceLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return ModResourceLocation.parse(GsonHelper.convertToString(json, "location"));
        }

        public JsonElement serialize(ModResourceLocation src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
    }
}