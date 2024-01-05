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
package org.incendo.cloud.processors.cooldown.listener;

import cloud.commandframework.Command;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.processors.cooldown.CooldownGroup;
import org.incendo.cloud.processors.cooldown.CooldownInstance;
import org.incendo.cloud.processors.cooldown.CooldownRepository;

/**
 * Cooldown creation listener that schedules a task to clean up the cooldown after it expires.
 *
 * @param <C> command sender type
 * @since 1.0.0
 */
@API(status = API.Status.STABLE, since = "1.0.0")
public final class ScheduledCleanupCreationListener<C> implements CooldownCreationListener<C> {

    private final ScheduledExecutorService executorService;
    private final CooldownRepository<C> cooldownRepository;

    /**
     * Creates a new scheduled cleanup listener.
     *
     * @param executorService    service to schedule the cleanup task with
     * @param cooldownRepository repository that the cooldown will be deleted from
     */
    public ScheduledCleanupCreationListener(
            final @NonNull ScheduledExecutorService executorService,
            final @NonNull CooldownRepository<C> cooldownRepository
    ) {
        this.executorService = executorService;
        this.cooldownRepository = cooldownRepository;
    }

    @Override
    public void cooldownCreated(
            final @NonNull C sender,
            final @NonNull Command<C> command,
            final @NonNull CooldownInstance instance
    ) {
        this.executorService.schedule(
                new CooldownDeletionTask(sender, instance.group()),
                instance.duration().toMillis(),
                TimeUnit.MILLISECONDS
        );
    }

    private final class CooldownDeletionTask implements Runnable {

        private final C sender;
        private final CooldownGroup group;

        private CooldownDeletionTask(final @NonNull C sender, final @NonNull CooldownGroup group) {
            this.sender = sender;
            this.group = group;
        }

        @Override
        public void run() {
            ScheduledCleanupCreationListener.this.cooldownRepository.deleteCooldown(this.sender, this.group);
        }
    }
}
