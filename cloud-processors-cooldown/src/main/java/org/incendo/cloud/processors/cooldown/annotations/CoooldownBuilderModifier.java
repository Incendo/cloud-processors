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
package org.incendo.cloud.processors.cooldown.annotations;

import cloud.commandframework.Command;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.annotations.BuilderModifier;
import java.time.Duration;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.processors.cooldown.CooldownGroup;
import org.incendo.cloud.processors.cooldown.DurationFunction;

/**
 * Builder modifier that will add the cooldown metadata to the command builder.
 *
 * @param <C> command sender type
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public final class CoooldownBuilderModifier<C> implements BuilderModifier<Cooldown, C> {

    /**
     * Creates a new builder modifier that will add the cooldown metadata to the command builder.
     *
     * @param <C> command sender type
     * @return the builder modifier
     */
    public static <C> @NonNull BuilderModifier<Cooldown, C> of() {
        return new CoooldownBuilderModifier<>();
    }

    /**
     * Adds the cooldown builder modifier to the given {@code annotationParser}.
     *
     * @param <C>              command sender type
     * @param annotationParser annotation parser to add the builder modifier to
     */
    public static <C> void install(final @NonNull AnnotationParser<C> annotationParser) {
        annotationParser.registerBuilderModifier(Cooldown.class, of());
    }

    private CoooldownBuilderModifier() {
    }

    @Override
    public Command.@NonNull Builder<? extends C> modifyBuilder(
            final @NonNull Cooldown annotation,
            final Command.@NonNull Builder<C> builder
    ) {
        final DurationFunction<C> duration = DurationFunction.constant(Duration.of(annotation.duration(), annotation.timeUnit()));
        final org.incendo.cloud.processors.cooldown.Cooldown<C> cooldown;
        if (annotation.group().isBlank()) {
            cooldown = org.incendo.cloud.processors.cooldown.Cooldown.<C>builder().duration(duration).build();
        } else {
            cooldown = org.incendo.cloud.processors.cooldown.Cooldown.<C>builder().duration(duration)
                    .group(CooldownGroup.named(annotation.group())).build();
        }
        return builder.apply(cooldown);
    }
}
