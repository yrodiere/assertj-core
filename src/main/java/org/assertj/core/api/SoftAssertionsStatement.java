/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2018 the original author or authors.
 */
package org.assertj.core.api;

import java.util.List;

import org.assertj.core.error.AssertionErrorCreator;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

// used for JUnit4 soft assertion rules
class SoftAssertionsStatement {

  private AbstractSoftAssertions soft;
  private AssertionErrorCreator assertionErrorCreator = new AssertionErrorCreator();

  private SoftAssertionsStatement(AbstractSoftAssertions soft) {
    this.soft = soft;
  }

  public static Statement softAssertionsStatement(AbstractSoftAssertions softAssertions, final Statement baseStatement) {
    return new SoftAssertionsStatement(softAssertions).build(baseStatement);
  }

  private Statement build(final Statement baseStatement) {
    // no lambda to keep java 6 compatibility
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        baseStatement.evaluate();
        List<Throwable> errors = soft.errorsCollected();
        if (errors.isEmpty()) return;
        // tests assertions raised some errors
        assertionErrorCreator.tryThrowingMultipleFailuresError(errors);
        // failed to throw MultipleFailuresError -> throw MultipleFailureException instead
        MultipleFailureException.assertEmpty(errors);
      }
    };
  }
}