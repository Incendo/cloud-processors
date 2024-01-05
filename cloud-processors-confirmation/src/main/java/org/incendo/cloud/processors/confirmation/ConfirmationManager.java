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

import cloud.commandframework.Command;
import cloud.commandframework.execution.CommandExecutionHandler;
import cloud.commandframework.execution.postprocessor.CommandPostprocessor;
import cloud.commandframework.keys.CloudKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.processors.cache.CloudCache;

/**
 * Manager for the confirmation system.
 *
 * @param <C> command sender type
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public final class ConfirmationManager<C> implements Command.Builder.Applicable<C> {

    /**
     * {@link cloud.commandframework.meta.CommandMeta} key that indicates that the command requires a confirmation
     * to be executed.
     */
    public static final CloudKey<Boolean> META_CONFIRMATION_REQUIRED = CloudKey.of(
            "cloud:require_confirmation",
            Boolean.class
    );

    /**
     * Creates a new confirmation manager using the given {@code configuration}.
     *
     * @param <C>           command sender type
     * @param configuration configuration for the confirmation manager
     * @return the created manager
     */
    public static <C> @NonNull ConfirmationManager<C> of(final @NonNull ConfirmationConfiguration<C> configuration) {
        return new ConfirmationManager<>(Objects.requireNonNull(configuration, "configuration"));
    }

    private final CloudCache<C, ConfirmationContext<C>> cache;
    private final ConfirmationConfiguration<C> configuration;

    private ConfirmationManager(final @NonNull ConfirmationConfiguration<C> configuration) {
        this.cache = configuration.cache();
        this.configuration = configuration;
    }

    /**
     * Returns the configuration for this manager.
     *
     * @return the configuration
     */
    public @NonNull ConfirmationConfiguration<C> configuration() {
        return this.configuration;
    }

    /**
     * Applies the {@link #META_CONFIRMATION_REQUIRED} meta key to the given {@code builder}.
     *
     * @param builder builder to decorate
     * @return  the decorated builder
     */
    @Override
    public Command.@NonNull Builder<C> applyToCommandBuilder(final Command.@NonNull Builder<C> builder) {
        return builder.meta(META_CONFIRMATION_REQUIRED, true);
    }

    /**
     * Returns a {@link CommandExecutionHandler} that will execute the pending command for the command sender,
     * or invoke the {@link ConfirmationConfiguration#noPendingCommandNotifier()}.
     *
     * @return confirmation execution handler
     */
    public @NonNull CommandExecutionHandler<C> createExecutionHandler() {
        return new ConfirmationExecutionHandler<>(this);
    }

    /**
     * Returns a {@link CommandPostprocessor} that will prevent commands from executing if they are marked
     * as needing confirmation.
     *
     * @return confirmation postprocessor
     */
    public @NonNull CommandPostprocessor<C> createPostprocessor() {
        return new ConfirmationPostprocessor<>(this);
    }

    /**
     * Removes the {@link ConfirmationContext} stored for the given {@code sender} and returns it.
     *
     * @param sender command sender
     * @return optional containing the value if it exists and has not yet expired
     */
    @NonNull Optional<ConfirmationContext<C>> popPending(final @NonNull C sender) {
        final ConfirmationContext<C> context = this.cache.popIfPresent(sender);
        if (context == null) {
            return Optional.empty();
        }

        final Duration expiration = this.configuration.expiration();
        if (expiration != null) {
            final Duration age = Duration.between(context.creationTime(), Instant.now());
            if (age.compareTo(expiration) > 0) {
                return Optional.empty();
            }
        }

        return Optional.of(context);
    }

    /**
     * Adds the given {@code context} as a pending command.
     *
     * @param sender  command sender
     * @param context confirmation context
     */
    void addPending(final @NonNull C sender, final @NonNull ConfirmationContext<C> context) {
        this.cache.put(sender, context);
    }
}
