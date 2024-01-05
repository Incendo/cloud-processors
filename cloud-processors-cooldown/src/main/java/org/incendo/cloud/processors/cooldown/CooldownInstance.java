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

import java.time.Duration;
import java.time.Instant;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.immutables.value.Value;
import org.incendo.cloud.processors.immutables.StagedImmutableBuilder;

/**
 * An instance of a {@link Cooldown}.
 *
 * @since 1.0.0
 */
@StagedImmutableBuilder
@Value.Immutable
@API(status = API.Status.STABLE, since = "1.0.0")
public interface CooldownInstance {

    /**
     * Returns a new cooldown instance builder.
     *
     * @return the builder
     */
    static ImmutableCooldownInstance.@NonNull GroupBuildStage builder() {
        return ImmutableCooldownInstance.builder();
    }

    /**
     * Returns the cooldown group.
     *
     * @return the group
     */
    @NonNull CooldownGroup group();

    /**
     * Returns the cooldown duration.
     *
     * @return the duration
     */
    @NonNull Duration duration();

    /**
     * Returns the creation time.
     *
     * @return the creation time
     */
    @NonNull Instant creationTime();
}
