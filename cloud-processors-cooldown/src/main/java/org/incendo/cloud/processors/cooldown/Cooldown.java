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
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.immutables.value.Value;
import org.incendo.cloud.processors.immutables.StagedImmutableBuilder;

/**
 * An instance of a cooldown for a {@link CooldownGroup} belonging to a command sender.
 *
 * @param <C> command sender type
 * @since 1.0.0
 */
@StagedImmutableBuilder
@Value.Immutable
@API(status = API.Status.STABLE, since = "1.0.0")
public interface Cooldown<C> extends Command.Builder.Applicable<C> {

    /**
     * Returns a new cooldown with the given {@code duration} using {@link CooldownGroup#command(Command)} as the group.
     *
     * @param <C>      command sender type
     * @param duration the duration
     * @return the cooldown
     */
    static <C> @NonNull Cooldown<C> of(final @NonNull DurationFunction<C> duration) {
        return Cooldown.<C>builder().duration(duration).build();
    }

    /**
     * Returns a new cooldown with the given {@code duration} and {@code group}.
     *
     * @param <C>      command sender type
     * @param duration the duration
     * @param group    cooldown group
     * @return the cooldown
     */
    static <C> @NonNull Cooldown<C> of(
            final @NonNull DurationFunction<C> duration,
            final @NonNull CooldownGroup group
    ) {
        return Cooldown.<C>builder().duration(duration).group(group).build();
    }

    /**
     * Returns a new cooldown builder.
     *
     * @param <C> command sender type
     * @return the builder
     */
    static <C> ImmutableCooldown.@NonNull DurationBuildStage<C> builder() {
        return ImmutableCooldown.builder();
    }

    /**
     * Returns the cooldown duration.
     *
     * @return the duration
     */
    @NonNull DurationFunction<C> duration();

    /**
     * Returns the group that this instance belongs to.
     * If set to {@code null} then {@link CooldownGroup#command(Command)} will be used.
     *
     * @return the cooldown group
     */
    default @Nullable CooldownGroup group() {
        return null;
    }

    @Override
    default Command.@NonNull Builder<C> applyToCommandBuilder(Command.@NonNull Builder<C> builder) {
        return builder.meta(CooldownManager.META_COOLDOWN_DURATION, this);
    }
}
