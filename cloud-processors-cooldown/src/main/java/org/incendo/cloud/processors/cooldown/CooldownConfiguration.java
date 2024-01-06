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

import cloud.commandframework.Command;
import cloud.commandframework.context.CommandContext;
import java.time.Clock;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Value;
import org.incendo.cloud.processors.cooldown.listener.CooldownActiveListener;
import org.incendo.cloud.processors.cooldown.listener.CooldownCreationListener;
import org.incendo.cloud.processors.cooldown.profile.CooldownProfileFactory;
import org.incendo.cloud.processors.cooldown.profile.StandardCooldownProfileFactory;
import org.incendo.cloud.processors.immutables.StagedImmutableBuilder;

/**
 * Configuration for a {@link CooldownManager}.
 *
 * @param <C> command sender type
 * @since 1.0.0
 */
@Value.Immutable
@StagedImmutableBuilder
@API(status = API.Status.STABLE, since = "1.0.0")
public interface CooldownConfiguration<C> {

    /**
     * Returns a new configuration builder.
     *
     * @param <C> command sender type
     * @return the builder
     */
    static <C> ImmutableCooldownConfiguration.@NonNull RepositoryBuildStage<C> builder() {
        return ImmutableCooldownConfiguration.builder();
    }

    /**
     * Returns the repository used to store cooldowns.
     *
     * @return the repository
     */
    @NonNull CooldownRepository<C> repository();

    /**
     * Returns the active cooldown listeners.
     *
     * <p>The active cooldown listeners are invoked when a cooldown is preventing a sender from executing a command.</p>
     *
     * @return active cooldown listeners
     */
    @NonNull List<CooldownActiveListener<C>> activeCooldownListeners();

    /**
     * Returns the creation listeners.
     *
     * <p>The creation listeners are invoked when a new cooldown is created.</p>
     *
     * @return creation listeners
     */
    @NonNull List<CooldownCreationListener<C>> creationListeners();

    /**
     * Returns a predicate that determines whether the {@link CommandContext}
     * should bypass the cooldown requirement.
     *
     * <p>The default implementation always returns {@code false}</p>
     *
     * @return predicate that determines whether cooldowns should be skipped
     */
    default @NonNull Predicate<@NonNull CommandContext<C>> bypassCooldown() {
        return context -> false;
    }

    /**
     * Returns the clock used to calculate the current time.
     *
     * @return the clock
     */
    default @NonNull Clock clock() {
        return Clock.systemUTC();
    }

    /**
     * Returns the factory that produces cooldown profiles.
     *
     * @return cooldown profile factory
     */
    default @NonNull CooldownProfileFactory profileFactory() {
        return new StandardCooldownProfileFactory(this);
    }

    /**
     * Returns the function that determines the fallback {@link CooldownGroup} in case no group has been provided
     * to the {@link Cooldown}.
     *
     * @return the fallback group function
     */
    default @NonNull Function<@NonNull Command<C>, @NonNull CooldownGroup> fallbackGroup() {
        return CooldownGroup::command;
    }
}
