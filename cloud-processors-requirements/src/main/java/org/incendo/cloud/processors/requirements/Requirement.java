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
package org.incendo.cloud.processors.requirements;

import cloud.commandframework.context.CommandContext;
import java.util.List;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A requirement for a command to be executed.
 *
 * @param <C> command sender type
 * @param <R> requirement type, used for inheritance
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public interface Requirement<C, R extends Requirement<C, R>> {

    /**
     * Returns whether the given {@code context} meets the requirement.
     *
     * @param commandContext command context to evaluate
     * @return {@code true} if the context meets the requirement, {@code false} if not
     */
    boolean evaluateRequirement(@NonNull CommandContext<C> commandContext);

    /**
     * Returns the parents of the requirement.
     *
     * <p>The parents will always be evaluated before {@code this} requirement.</p>
     *
     * @return the parents
     */
    default @NonNull List<@NonNull R> parents() {
        return List.of();
    }
}
