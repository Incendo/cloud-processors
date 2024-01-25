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

import io.leangen.geantyref.TypeToken;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.execution.postprocessor.CommandPostprocessor;
import org.incendo.cloud.key.CloudKey;

/**
 * Manager for the cooldown system.
 *
 * @param <C> command sender type
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public final class CooldownManager<C> {

    /**
     * Meta that adds the given cooldown to the command.
     */
    public static final CloudKey<Cooldown<?>> META_COOLDOWN_DURATION = CloudKey.of(
            "cloud:cooldown_duration",
            new TypeToken<Cooldown<?>>() {
            }
    );

    /**
     * Creates a new confirmation manager using the given {@code configuration}.
     *
     * @param <C>           command sender type
     * @param configuration configuration for the confirmation manager
     * @return the created manager
     */
    public static <C> @NonNull CooldownManager<C> of(final @NonNull CooldownConfiguration<C> configuration) {
        return new CooldownManager<>(Objects.requireNonNull(configuration, "configuration"));
    }

    private final CooldownRepository<C> repository;
    private final CooldownConfiguration<C> configuration;

    private CooldownManager(final @NonNull CooldownConfiguration<C> configuration) {
        this.repository = configuration.repository();
        this.configuration = configuration;
    }

    /**
     * Returns the configuration for this manager.
     *
     * @return the configuration
     */
    public @NonNull CooldownConfiguration<C> configuration() {
        return this.configuration;
    }

    /**
     * Returns the cooldown repository.
     *
     * @return the repository
     */
    public @NonNull CooldownRepository<C> repository() {
        return this.repository;
    }

    /**
     * Returns a {@link CommandPostprocessor} that will prevent commands from executing if they are marked
     * as needing confirmation.
     *
     * @return confirmation postprocessor
     */
    public @NonNull CommandPostprocessor<C> createPostprocessor() {
        return new CooldownPostprocessor<>(this);
    }
}
