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

import cloud.commandframework.execution.postprocessor.CommandPostprocessingContext;
import cloud.commandframework.execution.postprocessor.CommandPostprocessor;
import cloud.commandframework.services.types.ConsumerService;
import java.time.Duration;
import java.time.Instant;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * {@link CommandPostprocessor} for {@link CooldownManager}.
 *
 * @param <C> command sender type
 * @since 1.0.0
 */
@API(status = API.Status.INTERNAL, since = "1.0.0")
final class CooldownPostprocessor<C> implements CommandPostprocessor<C> {

    private final CooldownManager<C> confirmationManager;

    CooldownPostprocessor(final @NonNull CooldownManager<C> confirmationManager) {
        this.confirmationManager = confirmationManager;
    }

    @Override
    public void accept(final @NonNull CommandPostprocessingContext<C> context) {
        final Duration cooldownDuration = context.command().commandMeta()
                .getOrDefault(CooldownManager.META_COOLDOWN_DURATION, null);
        if (cooldownDuration == null) {
            return;
        }

        if (this.confirmationManager.configuration().bypassCooldown().test(context.commandContext())) {
            return;
        }

        final CooldownGroup group = context.command().commandMeta().getOrSupplyDefault(CooldownManager.META_COOLDOWN_GROUP,
                () -> CooldownGroup.command(context.command()));

        final CooldownProfile profile = this.confirmationManager.repository().getProfile(context.commandContext().sender());

        final Cooldown cooldown = profile.getCooldown(group);
        if (cooldown != null) {
            final Instant endTime = cooldown.creationTime().plus(cooldown.duration());
            this.confirmationManager.configuration().cooldownNotifier().notify(
                    context.commandContext().sender(),
                    cooldown,
                    Duration.between(Instant.now(this.confirmationManager.configuration().clock()), endTime)
            );
            ConsumerService.interrupt();
            return;
        }

        profile.setCooldown(
                group,
                Cooldown.builder()
                        .group(group)
                        .duration(cooldownDuration)
                        .creationTime(Instant.now(this.confirmationManager.configuration().clock()))
                        .build()
        );
    }
}
