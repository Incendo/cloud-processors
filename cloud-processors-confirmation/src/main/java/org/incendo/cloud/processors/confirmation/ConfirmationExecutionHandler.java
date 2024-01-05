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

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionHandler;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.apiguardian.api.API;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * {@link CommandExecutionHandler} for {@link ConfirmationManager}.
 *
 * @param <C> command sender type
 * @since 1.0.0
 */
@API(status = API.Status.INTERNAL, since = "1.0.0")
final class ConfirmationExecutionHandler<C> implements CommandExecutionHandler.FutureCommandExecutionHandler<C> {

    private final ConfirmationManager<C> confirmationManager;

    ConfirmationExecutionHandler(final @NonNull ConfirmationManager<C> confirmationManager) {
        this.confirmationManager = confirmationManager;
    }

    @Override
    public @NonNull CompletableFuture<Void> executeFuture(final @NonNull CommandContext<C> commandContext) {
        final Optional<ConfirmationContext<C>> pending = this.confirmationManager.popPending(commandContext.sender());
        if (pending.isEmpty()) {
            this.confirmationManager.configuration().noPendingCommandNotifier().accept(commandContext.sender());
            return CompletableFuture.completedFuture(null);
        }
        return pending.get().command().commandExecutionHandler().executeFuture(commandContext);
    }
}
