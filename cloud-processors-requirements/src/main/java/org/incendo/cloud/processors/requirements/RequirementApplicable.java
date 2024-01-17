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

import cloud.commandframework.Command;
import cloud.commandframework.keys.CloudKey;
import java.util.List;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Utility for adding {@link Requirements} to a {@link Command.Builder}.
 *
 * <p>The requirements can be applied to the command builder by
 * using {@link Command.Builder#apply(Command.Builder.Applicable)}.</p>
 *
 * @param <C> command sender type
 * @param <R> requirement type
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public final class RequirementApplicable<C, R extends Requirement<C, R>> implements Command.Builder.Applicable<C> {

    /**
     * Returns a new factory that creates {@link RequirementApplicable} instances.
     *
     * @param <C>            command sender type
     * @param <R>            requirement type
     * @param requirementKey key used to store the requirements in the command meta, should be the same as the key supplied to
     *                       {@link RequirementPostprocessor}
     * @return the factory
     */
    public static <C, R extends Requirement<C, R>> @NonNull RequirementApplicableFactory<C, R> factory(
            final @NonNull CloudKey<Requirements<C, R>> requirementKey
    ) {
        return new RequirementApplicableFactory<>(requirementKey);
    }

    private final CloudKey<Requirements<C, R>> requirementKey;
    private final Requirements<C, R> requirements;

    private RequirementApplicable(
            final @NonNull CloudKey<Requirements<C, R>> requirementKey,
            final @NonNull Requirements<C, R> requirements
    ) {
        this.requirementKey = Objects.requireNonNull(requirementKey, "requirementKey");
        this.requirements = Objects.requireNonNull(requirements, "requirements");
    }

    @Override
    public Command.@NonNull Builder<C> applyToCommandBuilder(final Command.@NonNull Builder<C> builder) {
        return builder.meta(this.requirementKey, this.requirements);
    }


    /**
     * Factory that produces {@link RequirementApplicable} instances.
     *
     * @param <C> command sender type
     * @param <R> requirement type
     * @since 1.0.0
     */
    @API(status = API.Status.STABLE, since = "1.0.0")
    public static final class RequirementApplicableFactory<C, R extends Requirement<C, R>> {

        private final CloudKey<Requirements<C, R>> requirementKey;

        private RequirementApplicableFactory(final @NonNull CloudKey<Requirements<C, R>> requirementKey) {
            this.requirementKey = Objects.requireNonNull(requirementKey, "requirementKey");
        }

        /**
         * Creates a new {@link RequirementApplicable} using the given {@code requirements}.
         *
         * @param requirements requirements to apply to the command builder
         * @return the {@link RequirementApplicable} instance
         */
        public @NonNull RequirementApplicable<C, R> create(final @NonNull Requirements<C, R> requirements) {
            Objects.requireNonNull(requirements, "requirements");
            return new RequirementApplicable<>(this.requirementKey, requirements);
        }

        /**
         * Creates a new {@link RequirementApplicable} using the given {@code requirements}.
         *
         * @param requirements requirements to apply to the command builder
         * @return the {@link RequirementApplicable} instance
         */
        public @NonNull RequirementApplicable<C, R> create(final @NonNull List<@NonNull R> requirements) {
            Objects.requireNonNull(requirements, "requirements");
            return new RequirementApplicable<>(this.requirementKey, Requirements.of(requirements));
        }

        /**
         * Creates a new {@link RequirementApplicable} using the given {@code requirements}.
         *
         * @param requirements requirements to apply to the command builder
         * @return the {@link RequirementApplicable} instance
         */
        @SafeVarargs
        @SuppressWarnings("varargs")
        public final @NonNull RequirementApplicable<C, R> create(final @NonNull R @NonNull... requirements) {
            Objects.requireNonNull(requirements, "requirements");
            return new RequirementApplicable<>(this.requirementKey, Requirements.of(requirements));
        }
    }
}
