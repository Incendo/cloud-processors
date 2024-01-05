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
import java.time.Instant;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Value;
import org.incendo.cloud.processors.immutables.ImmutableImpl;

/**
 * Context that stores information about a pending confirmation.
 *
 * @param <C> command sender type
 * @since 1.0.0
 */
@ImmutableImpl
@Value.Immutable
@API(status = API.Status.STABLE, since = "1.0.0")
public interface ConfirmationContext<C> {

    /**
     * Creates a new confirmation context.
     *
     * @param <C>          command sender type
     * @param creationTime time of creation
     * @param command      command that requires confirmation
     * @return the created context
     */
    static <C> @NonNull ConfirmationContext<C> of(
            final @NonNull Instant creationTime,
            final @NonNull Command<C> command
    ) {
        return ConfirmationContextImpl.of(creationTime, command);
    }

    /**
     * Returns the time the context was created.
     *
     * @return the creation time
     */
    @NonNull Instant creationTime();

    /**
     * Returns the command that requires confirmation.
     *
     * @return the command
     */
    @NonNull Command<C> command();
}
