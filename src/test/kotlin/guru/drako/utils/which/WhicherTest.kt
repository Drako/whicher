// Copyright 2017 Felix Bytow <drako@drako.guru>
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package guru.drako.utils.which

import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldEqual
import io.kotlintest.specs.StringSpec
import java.nio.file.Path
import java.nio.file.Paths

class WhicherTest : StringSpec() {
    object TestResolver : SystemResolver {
        override fun splitPath(path: String) = path.split(':').map { Paths.get(it) }.toTypedArray()
        override fun resolve(dir: Path, file: Path) = listOf(dir.resolve(file))
        override fun canExecute(file: Path) = when (file) {
            Paths.get("/usr/local/bin/ffmpeg") -> true
            Paths.get("/usr/local/bin/gcc") -> true
            Paths.get("/home/foo/bin/gcc") -> true
            else -> false
        }
    }

    companion object {
        val testWhicher = Whicher("/usr/bin:/usr/local/bin:/home/foo/bin", TestResolver)
    }

    init {
        "split should use correct split character" {
            testWhicher.path shouldBe arrayOf(
                Paths.get("/usr/bin"),
                Paths.get("/usr/local/bin"),
                Paths.get("/home/foo/bin")
            )
        }

        "which should return found program" {
            val expected = Paths.get("/usr/local/bin/ffmpeg")
            testWhicher.which("ffmpeg") shouldEqual expected
        }

        "allWhiches should return all found programs" {
            testWhicher.allWhiches("gcc") shouldBe arrayOf(
                Paths.get("/usr/local/bin/gcc"),
                Paths.get("/home/foo/bin/gcc")
            )
        }

        "silentWhich should return whether the program was found" {
            testWhicher.silentWhich("ffmpeg") shouldEqual true
            testWhicher.silentWhich("gcc") shouldEqual true
        }

        "the whiches shall not find programs that are not there" {
            testWhicher.which("gm") shouldEqual null
            testWhicher.allWhiches("gm").isEmpty() shouldEqual true
            testWhicher.silentWhich("gm") shouldEqual false
        }
    }
}
