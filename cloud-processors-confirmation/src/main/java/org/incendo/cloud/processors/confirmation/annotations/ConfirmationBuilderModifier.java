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
package org.incendo.cloud.processors.confirmation.annotations;

import cloud.commandframework.Command;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.annotations.BuilderModifier;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.processors.confirmation.ConfirmationManager;

/**
 * Builder modifier that will add the {@link ConfirmationManager#META_CONFIRMATION_REQUIRED}
 * meta value to the command builder.
 *
 * @param <C> command sender type
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public final class ConfirmationBuilderModifier<C> implements BuilderModifier<Confirmation, C> {

    /**
     * Creates a new builder modifier that will add the {@link ConfirmationManager#META_CONFIRMATION_REQUIRED}
     * meta value to the command builder.
     *
     * @param <C> command sender type
     * @return the builder modifier
     */
    public static <C> @NonNull BuilderModifier<Confirmation, C> of() {
        return new ConfirmationBuilderModifier<>();
    }

    /**
     * Adds the confirmation builder modifier to the given {@code annotationParser}.
     *
     * @param <C>              command sender type
     * @param annotationParser annotation parser to add the builder modifier to
     */
    public static <C> void install(final @NonNull AnnotationParser<C> annotationParser) {
        annotationParser.registerBuilderModifier(Confirmation.class, of());
    }

    private ConfirmationBuilderModifier() {
    }

    @Override
    public Command.@NonNull Builder<? extends C> modifyBuilder(
            final @NonNull Confirmation annotation,
            final Command.@NonNull Builder<C> builder
    ) {
        return builder.meta(ConfirmationManager.META_CONFIRMATION_REQUIRED, true);
    }
}
