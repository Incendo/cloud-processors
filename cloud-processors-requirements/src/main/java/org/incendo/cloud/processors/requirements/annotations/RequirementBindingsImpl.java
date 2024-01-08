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
package org.incendo.cloud.processors.requirements.annotations;

import cloud.commandframework.Command;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.annotations.BuilderModifier;
import cloud.commandframework.keys.CloudKey;
import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.function.Function;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.returnsreceiver.qual.This;
import org.incendo.cloud.processors.requirements.Requirement;
import org.incendo.cloud.processors.requirements.Requirements;

@API(status = API.Status.INTERNAL, consumers = "org.incendo.cloud.requirement.annotations.*", since = "1.0.0")
final class RequirementBindingsImpl<C, R extends Requirement<C, R>> implements RequirementBindings<C, R> {

    private final AnnotationParser<C> annotationParser;
    private final CloudKey<Requirements<C, R>> requirementKey;

    RequirementBindingsImpl(
            final @NonNull AnnotationParser<C> annotationParser,
            final @NonNull CloudKey<Requirements<C, R>> requirementKey
    ) {
        this.annotationParser = Objects.requireNonNull(annotationParser, "annotationParser");
        this.requirementKey = Objects.requireNonNull(requirementKey, "requirementKey");
    }

    @Override
    public @This @NonNull <A extends Annotation> RequirementBindings<C, R> register(
            final @NonNull Class<A> annotation,
            final @NonNull Function<A, R> requirement
    ) {
        this.annotationParser.registerBuilderModifier(annotation, new RequirementBuilderModifier<>(requirement));
        return this;
    }


    @API(status = API.Status.INTERNAL, consumers = "org.incendo.cloud.requirement.annotations.*", since = "1.0.0")
    private final class RequirementBuilderModifier<A extends Annotation> implements BuilderModifier<A, C> {

        private final Function<A, R> requirementFunction;

        private RequirementBuilderModifier(final @NonNull Function<A, R> requirementFunction) {
            this.requirementFunction = Objects.requireNonNull(requirementFunction, "requirementFunction");
        }

        @Override
        public Command.@NonNull Builder<? extends C> modifyBuilder(
                final @NonNull A annotation,
                final Command.@NonNull Builder<C> builder
        ) {
            final Requirements<C, R> requirements = builder.meta().getOrDefault(
                    RequirementBindingsImpl.this.requirementKey,
                    Requirements.empty()
            );
            return builder.meta(
                    RequirementBindingsImpl.this.requirementKey,
                    requirements.with(this.requirementFunction.apply(annotation))
            );
        }
    }
}
