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
package org.incendo.cloud.processors.cooldown.profile;

import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.processors.cooldown.CooldownGroup;
import org.incendo.cloud.processors.cooldown.CooldownInstance;

/**
 * Profile that stores {@link CooldownInstance cooldowns} identified by {@link CooldownGroup groups}.
 *
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public interface CooldownProfile {

    /**
     * Returns the cooldown for the given {@code group} if it exists and it's active.
     *
     * @param group group that identifies the cooldown
     * @return the active cooldown, or {@code null}
     */
    @Nullable CooldownInstance getCooldown(@NonNull CooldownGroup group);

    /**
     * Sets the cooldown for the given {@code group}.
     *
     * @param group    group that identifies the cooldown
     * @param cooldown cooldown value
     */
    void setCooldown(@NonNull CooldownGroup group, @NonNull CooldownInstance cooldown);

    /**
     * Deletes the cooldown for the given {@code group}, if it exists.
     *
     * @param group the group
     */
    void deleteCooldown(@NonNull CooldownGroup group);

    /**
     * Returns whether the profile is empty.
     *
     * @return {@code true} if the profile is empty, else {@code false}
     */
    boolean isEmpty();
}
