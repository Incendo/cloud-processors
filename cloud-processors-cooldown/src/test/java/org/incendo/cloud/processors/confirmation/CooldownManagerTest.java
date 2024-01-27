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

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.execution.CommandExecutionHandler;
import org.incendo.cloud.processors.confirmation.util.TestCommandManager;
import org.incendo.cloud.processors.confirmation.util.TestCommandSender;
import org.incendo.cloud.processors.cooldown.Cooldown;
import org.incendo.cloud.processors.cooldown.CooldownGroup;
import org.incendo.cloud.processors.cooldown.CooldownInstance;
import org.incendo.cloud.processors.cooldown.CooldownManager;
import org.incendo.cloud.processors.cooldown.CooldownRepository;
import org.incendo.cloud.processors.cooldown.DurationFunction;
import org.incendo.cloud.processors.cooldown.listener.CooldownActiveListener;
import org.incendo.cloud.processors.cooldown.listener.CooldownCreationListener;
import org.incendo.cloud.processors.cooldown.profile.CooldownProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.truth.Truth.assertThat;
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
    private CooldownActiveListener<TestCommandSender> notifier;
    @Mock(strictness = Mock.Strictness.LENIENT)
    private CommandExecutionHandler<TestCommandSender> commandExecutionHandler;
    @Mock
    private Clock clock;
    @Mock
    private CooldownCreationListener<TestCommandSender> listener;

    private CommandManager<TestCommandSender> commandManager;
    private CooldownManager<TestCommandSender> cooldownManager;

    @BeforeEach
    void setup() {
        this.commandManager = new TestCommandManager();
        this.cooldownManager = CooldownManager.cooldownManager(configBuilder ->
                configBuilder.repository(CooldownRepository.forMap(new HashMap<>()))
                        .addActiveCooldownListener(this.notifier)
                        .clock(this.clock)
                        .addCreationListener(this.listener));
        this.commandManager.registerCommandPostProcessor(this.cooldownManager.createPostprocessor());
    }

    @Test
    void testAppliesCooldown() {
        // Arrange
        final Cooldown<TestCommandSender> decorator = Cooldown.of(DurationFunction.constant(Duration.ofHours(1L)));
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
        verify(this.notifier).cooldownActive(eq(this.commandSender), any(), any(), any());
        verify(this.listener).cooldownCreated(eq(this.commandSender), any(), any());
    }

    @Test
    void testCooldownExpired() {
        // Arrange
        final Cooldown<TestCommandSender> decorator = Cooldown.of(DurationFunction.constant(Duration.ofHours(1L)));
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
        verify(this.notifier, never()).cooldownActive(eq(this.commandSender), any(), any(), any());
        verify(this.listener, times(2)).cooldownCreated(eq(this.commandSender), any(), any());
    }

    @Test
    void testCooldownGroupsDifferentGroups() {
        // Arrange
        final Cooldown<TestCommandSender> decorator1 = Cooldown.of(
                DurationFunction.constant(Duration.ofHours(1L)),
                CooldownGroup.named("foo")
        );
        final Cooldown<TestCommandSender> decorator2 = Cooldown.of(
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
        verify(this.notifier, never()).cooldownActive(eq(this.commandSender), any(), any(), any());
        verify(this.listener, times(2)).cooldownCreated(eq(this.commandSender), any(), any());
    }

    @Test
    void testCooldownGroupsSameGroup() {
        // Arrange
        final Cooldown<TestCommandSender> decorator = Cooldown.of(
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
        verify(this.notifier).cooldownActive(eq(this.commandSender), any(), any(), any());
        verify(this.listener).cooldownCreated(eq(this.commandSender), any(), any());
    }

    @Test
    void testCooldownDeletion() {
        // Arrange
        final CooldownGroup group = CooldownGroup.named("foo");
        final CooldownProfile profile = this.cooldownManager.repository()
                .getProfile(this.commandSender, this.cooldownManager.configuration().profileFactory());
        final CooldownInstance cooldown = CooldownInstance.builder()
                .profile(profile)
                .group(group)
                .duration(Duration.ofHours(1L))
                .creationTime(Instant.now())
                .build();
        profile.setCooldown(cooldown.group(), cooldown);

        // Act
        this.cooldownManager.repository().deleteCooldown(this.commandSender, group);

        // Assert
        assertThat(profile.isEmpty()).isTrue();
        assertThat(this.cooldownManager.repository().getProfileIfExists(this.commandSender)).isNull();
    }
}
