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

import cloud.commandframework.context.CommandContext;
import java.time.Duration;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A function that generates a duration from a command context.
 *
 * @param <C> command sender type
 * @since 1.0.0
 */
@FunctionalInterface
@API(status = API.Status.STABLE, since = "1.0.0")
public interface DurationFunction<C> {

    /**
     * Creates a new duration function that always returns {@code duration}.
     *
     * @param <C>      command sender type
     * @param duration constant duration
     * @return the duration function
     */
    static <C> @NonNull DurationFunction<C> constant(final @NonNull Duration duration) {
        return context -> duration;
    }

    /**
     * Returns the duration for the given {@code context}.
     *
     * @param context the context to get the duration for
     * @return the duration
     */
    @NonNull Duration getDuration(@NonNull CommandContext<C> context);
}
