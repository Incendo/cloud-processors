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
package org.incendo.cloud.processors.confirmation;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.incendo.cloud.Command;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.execution.CommandExecutionHandler;
import org.incendo.cloud.processors.cache.SimpleCache;
import org.incendo.cloud.processors.confirmation.util.TestCommandManager;
import org.incendo.cloud.processors.confirmation.util.TestCommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConfirmationManagerTest {

    @Mock
    private TestCommandSender commandSender;
    @Mock
    private Consumer<TestCommandSender> noPendingCommandNotifier;
    @Mock
    private BiConsumer<TestCommandSender, ConfirmationContext<TestCommandSender>> confirmationRequiredNotifier;
    @Mock(strictness = Mock.Strictness.LENIENT)
    private CommandExecutionHandler<TestCommandSender> commandExecutionHandler;

    private CommandManager<TestCommandSender> commandManager;
    private ConfirmationManager<TestCommandSender> confirmationManager;

    @BeforeEach
    void setup() {
        this.commandManager = new TestCommandManager();
        this.confirmationManager = ConfirmationManager.confirmationManager(
                configBuilder -> configBuilder
                        .cache(SimpleCache.of())
                        .noPendingCommandNotifier(this.noPendingCommandNotifier)
                        .confirmationRequiredNotifier(this.confirmationRequiredNotifier)
        );
        this.commandManager.registerCommandPostProcessor(this.confirmationManager.createPostprocessor());
    }

    @Test
    void testDecoration() {
        // Arrange
        final Command.Builder<TestCommandSender> builder = this.commandManager.commandBuilder("command");

        // Act
        final Command.Builder<TestCommandSender> result = builder.apply(this.confirmationManager);

        // Assert
        assertThat(result.meta().get(ConfirmationManager.META_CONFIRMATION_REQUIRED)).isTrue();
    }

    @Test
    void testRequiresConfirmation() {
        // Arrange
        this.commandManager.command(
                this.commandManager.commandBuilder("command")
                        .apply(this.confirmationManager)
                        .handler(this.commandExecutionHandler)
        );

        // Act
        this.commandManager.commandExecutor().executeCommand(this.commandSender, "command").join();

        // Assert
        verify(this.confirmationRequiredNotifier).accept(eq(this.commandSender), any());
        verify(this.commandExecutionHandler, never()).executeFuture(any());
    }

    @Test
    void testNoPendingConfirmation() {
        // Arrange
        this.commandManager.command(
                this.commandManager.commandBuilder("confirm")
                        .handler(this.confirmationManager.createExecutionHandler())
        );

        // Act
        this.commandManager.commandExecutor().executeCommand(this.commandSender, "confirm").join();

        // Assert
        verify(this.noPendingCommandNotifier).accept(this.commandSender);
    }

    @Test
    void testConfirmation() {
        // Arrange
        this.commandManager.command(
                this.commandManager.commandBuilder("command")
                        .apply(this.confirmationManager)
                        .handler(this.commandExecutionHandler)
        );
        this.commandManager.command(
                this.commandManager.commandBuilder("confirm")
                        .handler(this.confirmationManager.createExecutionHandler())
        );
        when(this.commandExecutionHandler.executeFuture(any())).thenReturn(CompletableFuture.completedFuture(null));

        // Act
        this.commandManager.commandExecutor().executeCommand(this.commandSender, "command").join();
        this.commandManager.commandExecutor().executeCommand(this.commandSender, "confirm").join();

        // Assert
        verify(this.confirmationRequiredNotifier).accept(eq(this.commandSender), any());
        verify(this.commandExecutionHandler).executeFuture(any());
    }
}
