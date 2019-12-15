package org.nself.gtrunner

import org.junit.jupiter.api.TestInstance
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class GTRunnerTest {
    val testResourcesDir =
        "${System.getProperty("user.dir")}${File.separator}test${File.separator}resources${File.separator}"
}