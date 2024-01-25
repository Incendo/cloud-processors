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

import java.time.Instant;
import java.util.Objects;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.execution.postprocessor.CommandPostprocessingContext;
import org.incendo.cloud.execution.postprocessor.CommandPostprocessor;
import org.incendo.cloud.services.type.ConsumerService;

/**
 * {@link CommandPostprocessor} for {@link ConfirmationManager}.
 *
 * @param <C> command sender type
 * @since 1.0.0
 */
@API(status = API.Status.INTERNAL, since = "1.0.0")
final class ConfirmationPostprocessor<C> implements CommandPostprocessor<C> {

    private final ConfirmationManager<C> confirmationManager;

    ConfirmationPostprocessor(final @NonNull ConfirmationManager<C> confirmationManager) {
        this.confirmationManager = Objects.requireNonNull(confirmationManager, "confirmationManager");
    }

    @Override
    public void accept(final @NonNull CommandPostprocessingContext<C> context) {
        if (!context.command().commandMeta().getOrDefault(ConfirmationManager.META_CONFIRMATION_REQUIRED, false)) {
            return;
        }

        if (this.confirmationManager.configuration().bypassConfirmation().test(context.commandContext())) {
            return;
        }

        final ConfirmationContext<C> confirmationContext = ConfirmationContext.of(Instant.now(), context.command());
        this.confirmationManager.addPending(context.commandContext().sender(), confirmationContext);
        this.confirmationManager.configuration().confirmationRequiredNotifier().accept(
                context.commandContext().sender(),
                confirmationContext
        );

        // This prevents the command from executing.
        ConsumerService.interrupt();
    }
}
