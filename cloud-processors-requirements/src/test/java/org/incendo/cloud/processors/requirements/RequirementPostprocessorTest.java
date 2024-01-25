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

import io.leangen.geantyref.TypeToken;
import java.util.concurrent.CompletableFuture;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.execution.CommandExecutionHandler;
import org.incendo.cloud.key.CloudKey;
import org.incendo.cloud.processors.requirements.util.TestCommandManager;
import org.incendo.cloud.processors.requirements.util.TestCommandSender;
import org.incendo.cloud.processors.requirements.util.TestRequirement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequirementPostprocessorTest {

    private static final CloudKey<Requirements<TestCommandSender, TestRequirement>> REQUIREMENTS_KEY = CloudKey.of(
            "requirements",
            new TypeToken<Requirements<TestCommandSender, TestRequirement>>() {
            }
    );

    @Mock
    private TestCommandSender commandSender;
    @Mock
    private RequirementFailureHandler<TestCommandSender, TestRequirement> failureHandler;
    @Mock
    private CommandExecutionHandler<TestCommandSender> executor;

    private CommandManager<TestCommandSender> commandManager;
    private RequirementApplicable.RequirementApplicableFactory<TestCommandSender, TestRequirement> factory;

    @BeforeEach
    void setup() {
        this.commandManager = new TestCommandManager();
        this.commandManager.registerCommandPostProcessor(RequirementPostprocessor.of(REQUIREMENTS_KEY, this.failureHandler));
        this.factory = RequirementApplicable.factory(REQUIREMENTS_KEY);
    }

    @Test
    void testPassing() {
        // Arrange
        final Requirements<TestCommandSender, TestRequirement> requirements = Requirements.of(TestRequirement.NAME_IS_FYODOR);
        this.commandManager.command(
                this.commandManager.commandBuilder("command")
                        .apply(this.factory.create(requirements))
                        .handler(this.executor)
        );
        when(this.commandSender.name()).thenReturn("Fyodor");
        when(this.executor.executeFuture(any())).thenReturn(CompletableFuture.completedFuture(null));

        // Act
        this.commandManager.commandExecutor().executeCommand(this.commandSender, "command").join();

        // Assert
        verify(this.executor).executeFuture(any());
        verify(this.commandSender, times(2)).name();
    }

    @Test
    void testFailing() {
        // Arrange
        final Requirements<TestCommandSender, TestRequirement> requirements = Requirements.of(TestRequirement.NAME_IS_FYODOR);
        this.commandManager.command(
                this.commandManager.commandBuilder("command")
                        .apply(this.factory.create(requirements))
                        .handler(this.executor)
        );
        when(this.commandSender.name()).thenReturn("Fjodor");

        // Act
        this.commandManager.commandExecutor().executeCommand(this.commandSender, "command").join();

        // Assert
        verify(this.executor, never()).executeFuture(any());
        verify(this.commandSender, times(2)).name();
        verify(this.failureHandler).handleFailure(any(), eq(TestRequirement.NAME_IS_FYODOR));
    }
}
