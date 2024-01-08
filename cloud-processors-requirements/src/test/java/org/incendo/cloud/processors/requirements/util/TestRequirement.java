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
package org.incendo.cloud.processors.requirements.util;

import cloud.commandframework.context.CommandContext;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.processors.requirements.Requirement;

public enum TestRequirement implements Requirement<TestCommandSender, TestRequirement> {
    HAS_NAME(Predicate.not(ctx -> ctx.sender().name().isBlank())),
    NAME_IS_FYODOR(ctx -> ctx.sender().name().equalsIgnoreCase("Fyodor"), HAS_NAME),
    NAME_IS_NOT_FYODOR(Predicate.not(ctx -> ctx.sender().name().equalsIgnoreCase("Fyodor")));

    private final Predicate<CommandContext<TestCommandSender>> predicate;
    private final List<TestRequirement> parents;

    TestRequirement(
            final @NonNull Predicate<CommandContext<TestCommandSender>> predicate,
            final @NonNull TestRequirement @NonNull... parents
    ) {
        this.predicate = predicate;
        this.parents = Arrays.asList(parents);
    }

    @Override
    public boolean evaluateRequirement(final @NonNull CommandContext<TestCommandSender> commandContext) {
        return this.predicate.test(commandContext);
    }

    @Override
    public @NonNull List<@NonNull TestRequirement> parents() {
        return this.parents;
    }
}
