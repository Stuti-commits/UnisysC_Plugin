/*
 * SonarQube Unisys C Plugin
 */
package org.sonar.c.checks;

import java.io.File;
import org.junit.Test;

public class EscapeSequenceTerminationCheckTest {

    private EscapeSequenceTerminationCheck check = new EscapeSequenceTerminationCheck();

    @Test
    public void test() {
        CVerifier.verify(
            new File("src/test/resources/checks/EscapeSequenceTerminationCheck.ccc_m"),
            check
        );
    }
}