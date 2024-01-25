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

import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.execution.postprocessor.CommandPostprocessingContext;
import org.incendo.cloud.execution.postprocessor.CommandPostprocessor;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.services.type.ConsumerService;

/**
 * {@link CommandPostprocessor} that checks for {@link Requirement requirements} before the commands are executed.
 *
 * @param <C> command sender type
 * @param <R> requirement type
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public final class RequirementPostprocessor<C, R extends Requirement<C, R>> implements CommandPostprocessor<C> {

    /**
     * Creates a new {@link RequirementPostprocessor}.
     *
     * @param <C>            command sender type
     * @param <R>            requirement type
     * @param requirementKey key that is used to store the requirements in the {@link org.incendo.cloud.meta.CommandMeta}
     * @param failureHandler handler that gets invoked when a requirement is unmet
     * @return the postprocessor
     */
    public static <C, R extends Requirement<C, R>> RequirementPostprocessor<C, R> of(
            final @NonNull CloudKey<Requirements<C, R>> requirementKey,
            final @NonNull RequirementFailureHandler<C, R> failureHandler
    ) {
        return new RequirementPostprocessor<>(requirementKey, failureHandler);
    }

    private final CloudKey<Requirements<C, R>> requirementKey;
    private final RequirementFailureHandler<C, R> failureHandler;

    private RequirementPostprocessor(
            final @NonNull CloudKey<Requirements<C, R>> requirementKey,
            final @NonNull RequirementFailureHandler<C, R> failureHandler
    ) {
        this.requirementKey = Objects.requireNonNull(requirementKey, "requirementKey");
        this.failureHandler = Objects.requireNonNull(failureHandler, "failureHandler");
    }

    @Override
    public void accept(final @NonNull CommandPostprocessingContext<C> context) {
        final Requirements<C, R> requirements = context.command().commandMeta().getOrDefault(this.requirementKey, null);
        if (requirements == null) {
            return;
        }

        for (final R requirement : requirements) {
            if (requirement.evaluateRequirement(context.commandContext())) {
                continue;
            }
            this.failureHandler.handleFailure(context.commandContext(), requirement);
            ConsumerService.interrupt();
        }
    }
}
