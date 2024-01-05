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

import cloud.commandframework.CommandManager;
import cloud.commandframework.execution.CommandExecutionHandler;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import org.incendo.cloud.processors.confirmation.util.TestCommandManager;
import org.incendo.cloud.processors.confirmation.util.TestCommandSender;
import org.incendo.cloud.processors.cooldown.CooldownConfiguration;
import org.incendo.cloud.processors.cooldown.CooldownDecorator;
import org.incendo.cloud.processors.cooldown.CooldownGroup;
import org.incendo.cloud.processors.cooldown.CooldownManager;
import org.incendo.cloud.processors.cooldown.CooldownNotifier;
import org.incendo.cloud.processors.cooldown.CooldownRepository;
import org.incendo.cloud.processors.cooldown.DurationFunction;
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
class CooldownManagerTest {

    @Mock
    private TestCommandSender commandSender;
    @Mock
    private CooldownNotifier<TestCommandSender> notifier;
    @Mock(strictness = Mock.Strictness.LENIENT)
    private CommandExecutionHandler<TestCommandSender> commandExecutionHandler;
    @Mock
    private Clock clock;

    private CommandManager<TestCommandSender> commandManager;
    private CooldownManager<TestCommandSender> cooldownManager;

    @BeforeEach
    void setup() {
        this.commandManager = new TestCommandManager();
        this.cooldownManager = CooldownManager.of(
                CooldownConfiguration.<TestCommandSender>builder()
                        .repository(CooldownRepository.forMap(new HashMap<>(), this.clock))
                        .cooldownNotifier(this.notifier)
                        .clock(this.clock)
                        .build()
        );
        this.commandManager.registerCommandPostProcessor(this.cooldownManager.createPostprocessor());
    }

    @Test
    void testAppliesCooldown() {
        // Arrange
        final CooldownDecorator<TestCommandSender> decorator = CooldownDecorator.of(DurationFunction
                .constant(Duration.ofHours(1L)));
        this.commandManager.command(
                this.commandManager.commandBuilder("command")
                        .apply(decorator)
                        .handler(this.commandExecutionHandler)
        );

        when(this.clock.instant()).thenReturn(Instant.now());
        when(this.commandExecutionHandler.executeFuture(any())).thenReturn(CompletableFuture.completedFuture(null));

        // Act
        this.commandManager.commandExecutor().executeCommand(this.commandSender, "command").join();
        this.commandManager.commandExecutor().executeCommand(this.commandSender, "command").join();

        // Assert
        verify(this.commandExecutionHandler).executeFuture(any());
        verify(this.notifier).notify(eq(this.commandSender), any(), any());
    }

    @Test
    void testCooldownExpired() {
        // Arrange
        final CooldownDecorator<TestCommandSender> decorator = CooldownDecorator.of(DurationFunction
                .constant(Duration.ofHours(1L)));
        this.commandManager.command(
                this.commandManager.commandBuilder("command")
                        .apply(decorator)
                        .handler(this.commandExecutionHandler)
        );

        when(this.clock.instant()).thenReturn(Instant.ofEpochSecond(0L)).thenReturn(Instant.now());
        when(this.commandExecutionHandler.executeFuture(any())).thenReturn(CompletableFuture.completedFuture(null));

        // Act
        this.commandManager.commandExecutor().executeCommand(this.commandSender, "command").join();
        this.commandManager.commandExecutor().executeCommand(this.commandSender, "command").join();

        // Assert
        verify(this.commandExecutionHandler, times(2)).executeFuture(any());
        verify(this.notifier, never()).notify(eq(this.commandSender), any(), any());
    }

    @Test
    void testCooldownGroupsDifferentGroups() {
        final CooldownDecorator<TestCommandSender> decorator1 = CooldownDecorator.of(
                DurationFunction.constant(Duration.ofHours(1L)),
                CooldownGroup.named("foo")
        );
        final CooldownDecorator<TestCommandSender> decorator2 = CooldownDecorator.of(
                DurationFunction.constant(Duration.ofHours(1L)),
                CooldownGroup.named("bar")
        );
        this.commandManager.command(
                this.commandManager.commandBuilder("command1")
                        .apply(decorator1)
                        .handler(this.commandExecutionHandler)
        );
        this.commandManager.command(
                this.commandManager.commandBuilder("command2")
                        .apply(decorator2)
                        .handler(this.commandExecutionHandler)
        );

        when(this.clock.instant()).thenReturn(Instant.now());
        when(this.commandExecutionHandler.executeFuture(any())).thenReturn(CompletableFuture.completedFuture(null));

        // Act
        this.commandManager.commandExecutor().executeCommand(this.commandSender, "command1").join();
        this.commandManager.commandExecutor().executeCommand(this.commandSender, "command2").join();

        // Assert
        verify(this.commandExecutionHandler, times(2)).executeFuture(any());
        verify(this.notifier, never()).notify(eq(this.commandSender), any(), any());
    }

    @Test
    void testCooldownGroupsSameGroup() {
        final CooldownDecorator<TestCommandSender> decorator = CooldownDecorator.of(
                DurationFunction.constant(Duration.ofHours(1L)),
                CooldownGroup.named("foo")
        );
        this.commandManager.command(
                this.commandManager.commandBuilder("command1")
                        .apply(decorator)
                        .handler(this.commandExecutionHandler)
        );
        this.commandManager.command(
                this.commandManager.commandBuilder("command2")
                        .apply(decorator)
                        .handler(this.commandExecutionHandler)
        );

        when(this.clock.instant()).thenReturn(Instant.now());
        when(this.commandExecutionHandler.executeFuture(any())).thenReturn(CompletableFuture.completedFuture(null));

        // Act
        this.commandManager.commandExecutor().executeCommand(this.commandSender, "command1").join();
        this.commandManager.commandExecutor().executeCommand(this.commandSender, "command2").join();

        // Assert
        verify(this.commandExecutionHandler, times(1)).executeFuture(any());
        verify(this.notifier).notify(eq(this.commandSender), any(), any());
    }
}
