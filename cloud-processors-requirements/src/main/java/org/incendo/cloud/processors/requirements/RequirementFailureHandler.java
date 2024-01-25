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

import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.context.CommandContext;

/**
 * Handler that gets invoked when a {@link Requirement requirement} is not met.
 *
 * @param <C> command sender type
 * @param <R> requirement type
 * @since 1.0.0
 */
@FunctionalInterface
@API(status = API.Status.STABLE, since = "1.0.0")
public interface RequirementFailureHandler<C, R extends Requirement<C, R>> {

    /**
     * Returns a requirement failure handler that does nothing.
     *
     * @param <C> command sender type
     * @param <R> requirement type
     * @return the handler
     */
    static <C, R extends Requirement<C, R>> @NonNull RequirementFailureHandler<C, R> noOp() {
        return (requirement, context) -> {};
    }

    /**
     * Handles the case where the given {@code context} does not meet the given {@code requirement}.
     *
     * @param context     the context
     * @param requirement the unmet requirement
     */
    void handleFailure(@NonNull CommandContext<C> context, @NonNull R requirement);
}
