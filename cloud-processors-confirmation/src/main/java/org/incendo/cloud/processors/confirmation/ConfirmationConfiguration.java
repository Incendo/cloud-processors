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
package org.incendo.cloud.processors.confirmation;

import java.time.Duration;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.immutables.value.Value;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.processors.cache.CloudCache;
import org.incendo.cloud.processors.immutables.StagedImmutableBuilder;

/**
 * Configuration for a {@link ConfirmationManager}.
 *
 * @param <C> command sender type
 * @since 1.0.0
 */
@Value.Immutable
@StagedImmutableBuilder
@API(status = API.Status.STABLE, since = "1.0.0")
public interface ConfirmationConfiguration<C> {

    /**
     * Returns a new configuration builder.
     *
     * @param <C> command sender type
     * @return the builder
     */
    static <C> ImmutableConfirmationConfiguration.@NonNull CacheBuildStage<C> builder() {
        return ImmutableConfirmationConfiguration.<C>builder();
    }

    /**
     * Returns the cache used to store pending commands.
     *
     * @return the cache
     */
    @NonNull CloudCache<C, ConfirmationContext<C>> cache();

    /**
     * Returns a consumer that gets invoked when a sender tries to invoke the confirmation handler
     * without having any pending commands.
     *
     * @return notifier for no pending commands
     */
    @NonNull Consumer<C> noPendingCommandNotifier();

    /**
     * Returns a consumer that gets invoked when a command requires confirmation.
     *
     * @return notifier for commands that require confirmation
     */
    @NonNull BiConsumer<C, ConfirmationContext<C>> confirmationRequiredNotifier();

    /**
     * Returns a predicate that determines whether the {@link CommandContext}
     * should bypass the confirmation requirement.
     *
     * <p>The default implementation always returns {@code false}</p>
     *
     * @return predicate that determines whether confirmation should be skipped
     */
    default @NonNull Predicate<@NonNull CommandContext<C>> bypassConfirmation() {
        return context -> false;
    }

    /**
     * Returns the time to keep the pending commands for. This can be set to {@code null} to
     * not enforce an expiration time.
     *
     * <p>It is recommended to configure the {@link #cache()} to match this value.</p>
     *
     * <p>The default value is {@code null}.</p>
     *
     * @return the expiration duration
     */
    default @Nullable Duration expiration() {
        return null;
    }
}
