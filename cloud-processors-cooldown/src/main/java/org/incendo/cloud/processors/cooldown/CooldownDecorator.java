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

/**
 * Utility for decorating {@link Command.Builder command builders}.
 *
 * <p>The decorator can be applied using {@link Command.Builder#apply(Command.Builder.Applicable)}.</p>
 *
 * @param <C> command sender type
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public final class CooldownDecorator<C> implements Command.Builder.Applicable<C> {

    /**
     * Returns a new decorator that uses the {@link CooldownGroup#command(Command)} group.
     *
     * @param <C>      command sender type
     * @param duration cooldown duration
     * @return the decorator
     */
    public static <C> @NonNull CooldownDecorator<C> of(
            final @NonNull DurationFunction<C> duration
    ) {
        return new CooldownDecorator<>(duration, null /* group */);
    }

    /**
     * Returns a new decorator.
     *
     * @param <C>      command sender type
     * @param duration cooldown duration
     * @param group    cooldown group
     * @return the decorator
     */
    public static <C> @NonNull CooldownDecorator<C> of(
            final @NonNull DurationFunction<C> duration,
            final @NonNull CooldownGroup group
    ) {
        return new CooldownDecorator<>(duration, group);
    }

    private final DurationFunction<C> duration;
    private final CooldownGroup group;

    private CooldownDecorator(final @NonNull DurationFunction<C> duration, final @Nullable CooldownGroup group) {
        this.duration = duration;
        this.group = group;
    }

    @Override
    public Command.@NonNull Builder<C> applyToCommandBuilder(final Command.@NonNull Builder<C> builder) {
        if (this.group != null) {
            return builder.meta(CooldownManager.META_COOLDOWN_DURATION, this.duration)
                    .meta(CooldownManager.META_COOLDOWN_GROUP, this.group);
        }
        return builder.meta(CooldownManager.META_COOLDOWN_DURATION, this.duration);
    }
}
