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

import cloud.commandframework.Command;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Groups cooldown together.
 *
 * <p>If two commands belong to the same group, the cooldown will be shared between those commands.</p>
 *
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public interface CooldownGroup {

    /**
     * Creates a new cooldown group that is unique to the given {@code command}.
     *
     * <p>This means that the cooldown will not be shared with any other commands.</p>
     *
     * @param command the command
     * @return the group
     */
    static CooldownGroup command(final @NonNull Command<?> command) {
        return new CommandCooldownGroup(command);
    }

    /**
     * Creates a new cooldown group identified by the given {@code name}.
     *
     * @param name group name
     * @return the group
     */
    static CooldownGroup named(final @NonNull String name) {
        return new NamedCooldownGroup(name);
    }

    final class CommandCooldownGroup implements CooldownGroup {

        private final Command<?> command;

        private CommandCooldownGroup(final @NonNull Command<?> command) {
            this.command = command;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final CommandCooldownGroup that = (CommandCooldownGroup) o;
            return Objects.equals(this.command, that.command);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.command);
        }
    }

    final class NamedCooldownGroup implements CooldownGroup {

        private final String name;

        private NamedCooldownGroup(final @NonNull String name) {
            this.name = name;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final NamedCooldownGroup that = (NamedCooldownGroup) o;
            return Objects.equals(this.name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.name);
        }
    }
}
