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
package org.incendo.cloud.processors.requirements.annotations;

import cloud.commandframework.CommandManager;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.annotations.Command;
import cloud.commandframework.execution.CommandExecutionHandler;
import cloud.commandframework.keys.CloudKey;
import io.leangen.geantyref.TypeToken;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.incendo.cloud.processors.requirements.RequirementFailureHandler;
import org.incendo.cloud.processors.requirements.RequirementPostprocessor;
import org.incendo.cloud.processors.requirements.Requirements;
import org.incendo.cloud.processors.requirements.util.TestCommandManager;
import org.incendo.cloud.processors.requirements.util.TestCommandSender;
import org.incendo.cloud.processors.requirements.util.TestRequirement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.google.common.truth.Truth.assertThat;

@ExtendWith(MockitoExtension.class)
class RequirementBindingsTest {

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
    private AnnotationParser<TestCommandSender> annotationParser;

    @BeforeEach
    void setup() {
        this.commandManager = new TestCommandManager();
        this.commandManager.registerCommandPostProcessor(RequirementPostprocessor.of(REQUIREMENTS_KEY, this.failureHandler));
        this.annotationParser = new AnnotationParser<>(this.commandManager, TestCommandSender.class);
    }

    @Test
    void testBindings() {
        // Arrange
        RequirementBindings.create(this.annotationParser, REQUIREMENTS_KEY).register(
                HasName.class,
                annotation -> TestRequirement.HAS_NAME
        );

        // Act
        final cloud.commandframework.Command<TestCommandSender> command =
                this.annotationParser.parse(new TestClass()).stream().findFirst().orElseThrow();

        // Assert
        assertThat(command.commandMeta().get(REQUIREMENTS_KEY)).containsExactly(TestRequirement.HAS_NAME);
    }


    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface HasName {
    }

    public static final class TestClass {

        @HasName
        @Command("command")
        public void command() {
        }
    }
}
