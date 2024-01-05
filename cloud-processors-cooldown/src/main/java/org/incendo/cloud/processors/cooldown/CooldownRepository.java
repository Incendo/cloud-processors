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
package org.incendo.cloud.processors.cooldown;

import java.time.Clock;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.processors.cache.CloudCache;

/**
 * Repository that stores {@link CooldownProfile cooldown profiles} identifies by keys of type {@link K}.
 *
 * <p>If the command sender objects are temporary (such as game entities, etc) the keys map be mapped to a persistent
 * identifier by using a {@link #mapping(Function, CooldownRepository)} repository.</p>
 *
 * @param <K> type of the keys that identifies the profiles
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public interface CooldownRepository<K> {

    /**
     * Returns a new repository backed by the given {@code otherRepository} that maps requests for keys
     * of type {@link C} to type {@link K} using the given {@code mappingFunction}.
     *
     * @param <C>             command sender type
     * @param <K>             repository key type
     * @param mappingFunction function that maps between the types
     * @param otherRepository backing repository
     * @return the repository
     */
    static <C, K> @NonNull CooldownRepository<C> mapping(
            final @NonNull Function<C, K> mappingFunction,
            final @NonNull CooldownRepository<K> otherRepository
    ) {
        return new MappingCooldownRepository<>(
                Objects.requireNonNull(mappingFunction, "mappingFunction"),
                Objects.requireNonNull(otherRepository, "otherRepository")
        );
    }

    /**
     * Returns a new repository backed by the given {@code cache}.
     *
     * @param <K>   key type
     * @param cache backing cache
     * @return the repository
     */
    static <K> @NonNull CooldownRepository<K> forCache(final @NonNull CloudCache<K, CooldownProfile> cache) {
        return new CacheCooldownRepository<>(Objects.requireNonNull(cache, "cache"), Clock.systemUTC());
    }

    /**
     * Returns a new repository backed by the given {@code cache}.
     *
     * @param <K>   key type
     * @param cache backing cache
     * @param clock clock used to retrieve current time
     * @return the repository
     */
    static <K> @NonNull CooldownRepository<K> forCache(
            final @NonNull CloudCache<K, CooldownProfile> cache,
            final @NonNull Clock clock
    ) {
        return new CacheCooldownRepository<>(Objects.requireNonNull(cache, "cache"), Objects.requireNonNull(clock, "clock"));
    }

    /**
     * Returns a new repository backed by the given {@code map}.
     *
     * @param <K> key type
     * @param map backing map
     * @return the repository
     */
    static <K> @NonNull CooldownRepository<K> forMap(final @NonNull Map<K, CooldownProfile> map) {
        return new MapCooldownRepository<>(Objects.requireNonNull(map, "map"), Clock.systemUTC());
    }

    /**
     * Returns a new repository backed by the given {@code map}.
     *
     * @param <K>   key type
     * @param map   backing map
     * @param clock clock used to retrieve current time
     * @return the repository
     */
    static <K> @NonNull CooldownRepository<K> forMap(
            final @NonNull Map<K, CooldownProfile> map,
            final @NonNull Clock clock
    ) {
        return new MapCooldownRepository<>(Objects.requireNonNull(map, "map"), Objects.requireNonNull(clock, "clock"));
    }

    /**
     * Returns the profile for the given {@code key}.
     *
     * <p>If no profile exists in the repository, a new profile will be persisted and returned.</p>
     *
     * @param key key that identifies the profile
     * @return the profile
     */
    @NonNull CooldownProfile getProfile(@NonNull K key);


    final class MappingCooldownRepository<C, K> implements CooldownRepository<C> {

        private final Function<C, K> mappingFunction;
        private final CooldownRepository<K> otherRepository;


        private MappingCooldownRepository(
                final @NonNull Function<C, K> mappingFunction,
                final @NonNull CooldownRepository<K> otherRepository
        ) {
            this.mappingFunction = mappingFunction;
            this.otherRepository = otherRepository;
        }

        @Override
        public @NonNull CooldownProfile getProfile(final @NonNull C key) {
            return this.otherRepository.getProfile(this.mappingFunction.apply(key));
        }
    }

    final class MapCooldownRepository<K> implements CooldownRepository<K> {

        private final Map<K, CooldownProfile> map;
        private final Clock clock;

        private MapCooldownRepository(
                final @NonNull Map<K, CooldownProfile> map,
                final @NonNull Clock clock
        ) {
            this.map = map;
            this.clock = clock;
        }

        @Override
        public @NonNull CooldownProfile getProfile(final @NonNull K key) {
            return this.map.computeIfAbsent(key, k -> CooldownProfile.empty(this.clock));
        }
    }

    final class CacheCooldownRepository<K> implements CooldownRepository<K> {

        private final CloudCache<K, CooldownProfile> cache;
        private final Clock clock;

        private CacheCooldownRepository(
                final @NonNull CloudCache<K, CooldownProfile> cache,
                final @NonNull Clock clock
        ) {
            this.cache = cache;
            this.clock = clock;
        }

        @Override
        public @NonNull CooldownProfile getProfile(final @NonNull K key) {
            CooldownProfile profile = this.cache.getIfPresent(key);
            if (profile == null) {
                profile = CooldownProfile.empty(this.clock);
                this.cache.put(key, profile);
            }
            return profile;
        }
    }
}
