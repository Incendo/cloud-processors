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

import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.incendo.cloud.processors.requirements.util.TestRequirement;
import org.incendo.cloud.processors.requirements.util.TestCommandSender;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

class RequirementsTest {

    @Test
    void requirementsAreUnmodifiable() {
        // Arrange
        final Requirements<TestCommandSender, TestRequirement> requirements = Requirements.of(TestRequirement.NAME_IS_FYODOR);

        // Act & Assert
        final List<@NonNull TestRequirement> result = requirements.requirements();
        assertThrows(UnsupportedOperationException.class, () -> result.remove(0));
    }

    @Test
    void expectedRequirementsAreReturned() {
        // Arrange
        final Requirements<TestCommandSender, TestRequirement> requirements = Requirements.of(TestRequirement.values());

        // Act
        final List<@NonNull TestRequirement> result = requirements.requirements();

        // Assert
        assertThat(result).containsExactlyElementsIn(TestRequirement.values());
    }

    @Test
    void parentsAreExpanded() {
        // Arrange
        final Requirements<TestCommandSender, TestRequirement> requirements = Requirements.of(TestRequirement.NAME_IS_FYODOR);

        // Act
        final List<@NonNull TestRequirement> result = requirements.requirements();

        // Assert
        assertThat(result).containsExactly(TestRequirement.HAS_NAME, TestRequirement.NAME_IS_FYODOR);
    }

    @Test
    void duplicatesAreIgnored() {
        // Arrange
        final Requirements<TestCommandSender, TestRequirement> requirements = Requirements.of(
                TestRequirement.HAS_NAME,
                TestRequirement.HAS_NAME,
                TestRequirement.HAS_NAME
        );

        // Act
        final List<@NonNull TestRequirement> result = requirements.requirements();

        // Assert
        assertThat(result).containsExactly(TestRequirement.HAS_NAME);
    }
}
