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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Immutable container of {@link Requirement requirements}.
 *
 * @param <C> command sender type
 * @param <R> requirement type
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public interface Requirements<C, R extends Requirement<C, R>> extends Iterable<@NonNull R> {

    private static <C, R extends Requirement<C, R>> @NonNull List<@NonNull R> extractRequirements(
            final @NonNull List<@NonNull R> requirements
    ) {
        Objects.requireNonNull(requirements, "requirements");
        final List<R> extractedRequirements = new ArrayList<>();
        for (final R requirement : requirements) {
            Objects.requireNonNull(requirement, "requirement");
            for (final R parentRequirement : extractRequirements(requirement.parents())) {
                if (!extractedRequirements.contains(parentRequirement)) {
                    extractedRequirements.add(parentRequirement);
                }
            }
            if (!extractedRequirements.contains(requirement)) {
                extractedRequirements.add(requirement);
            }
        }
        return List.copyOf(extractedRequirements);
    }

    /**
     * Creates an empty requirement container.
     *
     * @param <C>          command sender type
     * @param <R>          requirement type
     * @return the requirement container
     */
    static <C, R extends Requirement<C, R>> @NonNull Requirements<C, R> empty() {
        return new RequirementsImpl<C, R>(List.of());
    }

    /**
     * Creates a new immutable requirement container.
     *
     * @param <C>          command sender type
     * @param <R>          requirement type
     * @param requirements list of requirements
     * @return the requirement container
     */
    static <C, R extends Requirement<C, R>> @NonNull Requirements<C, R> of(
            final @NonNull List<@NonNull R> requirements
    ) {
        return new RequirementsImpl<>(extractRequirements(requirements));
    }

    /**
     * Creates a new immutable requirement container.
     *
     * @param <C>          command sender type
     * @param <R>          requirement type
     * @param requirements list of requirements
     * @return the requirement container
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    static <C, R extends Requirement<C, R>> @NonNull Requirements<C, R> of(
            final @NonNull R @NonNull... requirements
    ) {
        return of(Arrays.asList(requirements));
    }

    /**
     * Returns a new {@link Requirements} instance with the current {@link #requirements()} and the new {@code requirement}.
     *
     * @param requirement new requirement
     * @return the new instance
     */
    default @NonNull Requirements<C, R> with(final @NonNull R requirement) {
        final List<@NonNull R> requirements = new ArrayList<>(this.requirements());
        requirements.add(requirement);
        return of(requirements);
    }

    /**
     * Returns the requirements.
     *
     * @return immutable collection of requirements
     */
    @NonNull List<@NonNull R> requirements();

    /**
     * Returns an iterator that iterates over the requirements.
     *
     * @return the iterator
     */
    @Override
    default @NonNull Iterator<@NonNull R> iterator() {
        return this.requirements().iterator();
    }
}
