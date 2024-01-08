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

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.processors.cache.CloudCache;
import org.incendo.cloud.processors.cooldown.profile.CooldownProfile;
import org.incendo.cloud.processors.cooldown.profile.CooldownProfileFactory;

/**
 * Repository that stores {@link CooldownProfile cooldown profiles} identifies by keys of type {@link K}.
 *
 * <p>If the command sender objects are temporary (such as game entities, etc) the keys map be mapped to a persistent
 * identifier by using a {@link #mapping(Function, CooldownRepository)} repository.</p>
 *
 * <p>The {@link CooldownProfile} creation may be customized by passing a custom {@link CooldownProfileFactory}
 * to {@link CooldownConfiguration#profileFactory()}.</p>
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
        return new CacheCooldownRepository<>(Objects.requireNonNull(cache, "cache"));
    }

    /**
     * Returns a new repository backed by the given {@code map}.
     *
     * @param <K> key type
     * @param map backing map
     * @return the repository
     */
    static <K> @NonNull CooldownRepository<K> forMap(final @NonNull Map<K, CooldownProfile> map) {
        return new MapCooldownRepository<>(Objects.requireNonNull(map, "map"));
    }

    /**
     * Returns the profile for the given {@code key}.
     *
     * <p>If no profile exists in the repository, a new profile will be persisted and returned.</p>
     *
     * @param key            key that identifies the profile
     * @param profileFactory factory that creates profiles
     * @return the profile
     */
    @NonNull CooldownProfile getProfile(@NonNull K key, @NonNull CooldownProfileFactory profileFactory);

    /**
     * Returns the profile for the given {@code key}, if it exists.
     *
     * @param key key that identifies the profile
     * @return the profile, or {@code null}
     */
    @Nullable CooldownProfile getProfileIfExists(@NonNull K key);

    /**
     * Deletes the profile identified by the given {@code key}.
     *
     * @param key the key
     */
    void deleteProfile(@NonNull K key);

    /**
     * Deletes the cooldown identified by the given {@code key} belonging to the given {@code group}.
     *
     * <p>If the profile is empty after the deletion, then the profile is deleted too.</p>
     *
     * @param key   key identifying the profile
     * @param group group to delete
     */
    void deleteCooldown(@NonNull K key, @NonNull CooldownGroup group);


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
        public @NonNull CooldownProfile getProfile(
                final @NonNull C key,
                final @NonNull CooldownProfileFactory profileFactory
        ) {
            return this.otherRepository.getProfile(this.mappingFunction.apply(key), profileFactory);
        }

        @Override
        public @Nullable CooldownProfile getProfileIfExists(final @NonNull C key) {
            return this.otherRepository.getProfileIfExists(this.mappingFunction.apply(key));
        }

        @Override
        public  void deleteProfile(final @NonNull C key) {
            this.otherRepository.deleteProfile(this.mappingFunction.apply(key));
        }

        @Override
        public void deleteCooldown(final @NonNull C key, final @NonNull CooldownGroup group) {
            this.otherRepository.deleteCooldown(this.mappingFunction.apply(key), group);
        }
    }

    abstract class AbstractCooldownRepository<K> implements CooldownRepository<K> {

        @Override
        public synchronized void deleteCooldown(final @NonNull K key, final @NonNull CooldownGroup group) {
            final CooldownProfile profile = this.getProfileIfExists(key);
            if (profile == null) {
                return;
            }
            profile.deleteCooldown(group);
            if (profile.isEmpty()) {
                this.deleteProfile(key);
            }
        }
    }

    final class MapCooldownRepository<K> extends AbstractCooldownRepository<K> {

        private final Map<K, CooldownProfile> map;

        private MapCooldownRepository(final @NonNull Map<K, CooldownProfile> map) {
            this.map = map;
        }

        @Override
        public synchronized @NonNull CooldownProfile getProfile(
                final @NonNull K key,
                final @NonNull CooldownProfileFactory profileFactory
        ) {
            return this.map.computeIfAbsent(key, k -> profileFactory.create());
        }

        @Override
        public synchronized @Nullable CooldownProfile getProfileIfExists(final @NonNull K key) {
            return this.map.get(key);
        }

        @Override
        public synchronized void deleteProfile(final @NonNull K key) {
            this.map.remove(key);
        }
    }

    final class CacheCooldownRepository<K> extends AbstractCooldownRepository<K> {

        private final CloudCache<K, CooldownProfile> cache;

        private CacheCooldownRepository(final @NonNull CloudCache<K, CooldownProfile> cache) {
            this.cache = cache;
        }

        @Override
        public synchronized @NonNull CooldownProfile getProfile(
                final @NonNull K key,
                final @NonNull CooldownProfileFactory profileFactory
        ) {
            CooldownProfile profile = this.cache.getIfPresent(key);
            if (profile == null) {
                profile = profileFactory.create();
                this.cache.put(key, profile);
            }
            return profile;
        }

        @Override
        public synchronized @Nullable CooldownProfile getProfileIfExists(final @NonNull K key) {
            return this.cache.getIfPresent(key);
        }

        @Override
        public synchronized void deleteProfile(final @NonNull K key) {
            this.cache.delete(key);
        }
    }
}
