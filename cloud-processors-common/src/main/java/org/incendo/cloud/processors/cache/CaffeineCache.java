//
// MIT License
//
// Copyright (c) 2024 Incendo
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package org.incendo.cloud.processors.cache;

import com.github.benmanes.caffeine.cache.Cache;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * An implementation of {@link CloudCache} backed by a Caffeine {@link Cache}.
 *
 * @param <K> key type
 * @param <V> value type
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public final class CaffeineCache<K, V> implements CloudCache<K, V> {

    /**
     * Creates a new {@link CaffeineCache} backed by the given Guava {@code cache}.
     *
     * @param <K>   key type
     * @param <V>   value type
     * @param cache backing cache
     * @return the created cache instance
     */
    public static <K, V> @NonNull CloudCache<K, V> of(final @NonNull Cache<K, V> cache) {
        return new CaffeineCache<>(cache);
    }

    private final Cache<K, V> cache;

    private CaffeineCache(final @NonNull Cache<K, V> cache) {
        this.cache = Objects.requireNonNull(cache, "cache");
    }

    @Override
    public void delete(final @NonNull K key) {
        Objects.requireNonNull(key, "key");
        this.cache.invalidate(key);
    }

    @Override
    public void put(final @NonNull K key, final @NonNull V value) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");
        this.cache.put(key, value);
    }

    @Override
    public @Nullable V getIfPresent(final @NonNull K key) {
        Objects.requireNonNull(key, "key");
        return this.cache.getIfPresent(key);
    }
}
