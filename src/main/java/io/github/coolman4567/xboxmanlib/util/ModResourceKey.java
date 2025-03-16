package io.github.coolman4567.xboxmanlib.util;

import com.google.common.collect.MapMaker;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;


import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

public class ModResourceKey<T> implements Comparable<ModResourceKey<?>> {
    private static final ConcurrentMap<ModResourceKey.InternKey, ModResourceKey<?>> VALUES = new MapMaker().weakValues().makeMap();
    /**
     * The name of the parent registry of the resource.
     */
    private final ModResourceLocation registryName;
    /**
     * The location of the resource within the registry.
     */
    private final ModResourceLocation location;

    public static <T> Codec<ModResourceKey<T>> codec(ModResourceKey<? extends Registry<T>> registryKey) {
        return ModResourceLocation.CODEC.xmap(p_195979_ -> create(registryKey, p_195979_), ModResourceKey::location);
    }

    public static <T> StreamCodec<ByteBuf, ModResourceKey<T>> streamCodec(ModResourceKey<? extends Registry<T>> registryKey) {
        return ModResourceLocation.STREAM_CODEC.map(p_319559_ -> create(registryKey, p_319559_), ModResourceKey::location);
    }

    /**
     * Constructs a new {@code ResourceKey} for a resource with the specified {@code location} within the registry specified by the given {@code registryKey}.
     *
     * @return the created resource key. The registry name is set to the location of the specified {@code registryKey} and with the specified {@code location} as the location of the resource.
     */
    public static <T> ModResourceKey<T> create(ModResourceKey<? extends Registry<T>> registryKey, ModResourceLocation location) {
        return create(registryKey.location, location);
    }

    /**
     * @return the created registry key. The registry name is set to {@code minecraft:root} and the location the specified {@code registryName}.
     */
    public static <T> ModResourceKey<Registry<T>> createRegistryKey(ModResourceLocation location) {
        return create(ModRegistries.ROOT_REGISTRY_NAME, location);
    }

    private static <T> ModResourceKey<T> create(ModResourceLocation registryName, ModResourceLocation location) {
        return (ModResourceKey<T>)VALUES.computeIfAbsent(
                new ModResourceKey.InternKey(registryName, location), p_258225_ -> new ModResourceKey(p_258225_.registry, p_258225_.location)
        );
    }

    private ModResourceKey(ModResourceLocation registryName, ModResourceLocation location) {
        this.registryName = registryName;
        this.location = location;
    }

    @Override
    public String toString() {
        return "ResourceKey[" + this.registryName + " / " + this.location + "]";
    }

    /**
     * @return {@code true} if this resource key is a direct child of the specified {@code registryKey}.
     */
    public boolean isFor(ModResourceKey<? extends Registry<?>> ModregistryKey) {
        return this.registryName.equals(ModregistryKey.location());
    }

    public <E> Optional<ModResourceKey<E>> cast(ModResourceKey<? extends Registry<E>> registryKey) {
        return this.isFor(registryKey) ? Optional.of((ModResourceKey<E>)this) : Optional.empty();
    }

    public ModResourceLocation location() {
        return this.location;
    }

    public ModResourceLocation registry() {
        return this.registryName;
    }

    public ModResourceKey<Registry<T>> registryKey() {
        return createRegistryKey(this.registryName);
    }

    @Override
    public int compareTo(ModResourceKey<?> o) {
        int ret = this.registry().compareTo(o.registry());
        if (ret == 0) ret = this.location().compareTo(o.location());
        return ret;
    }

    static record InternKey(ModResourceLocation registry, ModResourceLocation location) {
    }
}