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

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Implementation of {@link CloudCache} backed by a {@link WeakHashMap}. It is <b>not</b> recommended to
 * use this implementation, and it should only be used if {@link CaffeineCache} or {@link GuavaCache} may
 * not be used.
 *
 * <p>This will not enforce any size constraints.</p>
 *
 * @param <K> key type
 * @param <V> value type
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public final class SimpleCache<K, V> implements CloudCache<K, V> {

    /**
     * Creates a new {@link SimpleCache} instance.
     *
     * @param <K> key type
     * @param <V> value type
     * @return the created cache instance
     */
    public static <K, V> @NonNull CloudCache<K, V> of() {
        return new SimpleCache<>();
    }

    private final Map<K, V> map = Collections.synchronizedMap(new WeakHashMap<>());

    @Override
    public void delete(final @NonNull K key) {
        Objects.requireNonNull(key, "key");
        this.map.remove(key);
    }

    @Override
    public void put(final @NonNull K key, final @NonNull V value) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");
        this.map.put(key, value);
    }

    @Override
    public @Nullable V getIfPresent(final @NonNull K key) {
        Objects.requireNonNull(key, "key");
        return this.map.get(key);
    }

    @Override
    public @Nullable V popIfPresent(final @NonNull K key) {
        Objects.requireNonNull(key, "key");
        return this.map.remove(key);
    }
}
