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

import java.util.Optional;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Something that caches values.
 *
 * <p>Standard implementations:<ul>
 *     <li>{@link GuavaCache}: Implementation for Guava</li>
 *     <li>{@link CaffeineCache}: Implementation for Caffeine</li>
 * </ul>
 *
 * @param <K> key type
 * @param <V> value type
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public interface CloudCache<K, V> {

    /**
     * Deletes the entry identified by the given {@code key}.
     *
     * @param key the key
     */
    void delete(@NonNull K key);

    /**
     * Stores the given {@code value} identified by the given {@code key}.
     *
     * @param key   the key
     * @param value the value
     */
    void put(@NonNull K key, @NonNull V value);

    /**
     * Returns the value identified by the given {@code key}, if it exists.
     *
     * @param key the key
     * @return the value, or {@code null}
     */
    @Nullable V getIfPresent(@NonNull K key);

    /**
     * Deletes the value identified by the given {@code key} and returns the value.
     *
     * @param key the key
     * @return the value, or {@code null}
     */
    default @Nullable V popIfPresent(@NonNull K key) {
        final V value = this.getIfPresent(key);
        if (value != null) {
            this.delete(key);
        }
        return value;
    }

    /**
     * Returns the value identified by the given {@code key}.
     *
     * @param key the key
     * @return the value
     */
    default @NonNull Optional<V> get(final @NonNull K key) {
        return Optional.ofNullable(this.getIfPresent(key));
    }
}
