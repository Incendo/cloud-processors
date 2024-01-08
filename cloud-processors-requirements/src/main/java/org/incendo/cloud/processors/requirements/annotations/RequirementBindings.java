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

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.keys.CloudKey;
import java.lang.annotation.Annotation;
import java.util.function.Function;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.common.returnsreceiver.qual.This;
import org.incendo.cloud.processors.requirements.RequirementPostprocessor;
import org.incendo.cloud.processors.requirements.Requirements;
import org.incendo.cloud.processors.requirements.Requirement;

/**
 * Utility for binding annotations to {@link Requirement requirements}.
 *
 * @param <C> command sender type
 * @param <R> requirement type
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public interface RequirementBindings<C, R extends Requirement<C, R>> {

    /**
     * Creates a new {@link RequirementBindings} instance.
     *
     * @param <C>              command sender type
     * @param <R>              requirement type
     * @param annotationParser annotation parser
     * @param requirementKey   key used to store the requirements in the command meta, should be the same as the key supplied to
     *                         {@link RequirementPostprocessor}
     * @return the bindings instance
     */
    static <C, R extends Requirement<C, R>> @NonNull RequirementBindings<C, R> create(
            final @NonNull AnnotationParser<C> annotationParser,
            final @NonNull CloudKey<Requirements<C, R>> requirementKey
    ) {
        return new RequirementBindingsImpl<>(annotationParser, requirementKey);
    }

    /**
     * Registers a new binding for the given {@code annotation} to the given {@code requirement}.
     *
     * @param <A>         annotation type
     * @param annotation  annotation class
     * @param requirement function that returns the requirement
     * @return {@code this}
     */
    @This @NonNull <A extends Annotation> RequirementBindings<C, R> register(
            @NonNull Class<A> annotation,
            @NonNull Function<A, R> requirement
    );
}
