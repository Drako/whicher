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

package guru.drako.which

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WhicherTest {
    companion object : SystemResolver {
        override fun splitPath(path: String) = path.split(':').toTypedArray()
        override fun resolve(dir: String, file: String) = listOf(dir + "/" + file)
        override fun canExecute(filename: String) = when (filename) {
            "/usr/local/bin/ffmpeg" -> true
            "/usr/local/bin/gcc" -> true
            "/home/foo/bin/gcc" -> true
            else -> false
        }

        val testWhicher = Whicher("/usr/bin:/usr/local/bin:/home/foo/bin", this)
    }

    @Test
    fun split() {
        val expected = arrayOf("/usr/bin", "/usr/local/bin", "/home/foo/bin")
        val actual = testWhicher.path
        assertTrue(expected contentEquals actual)
    }

    @Test
    fun which() {
        val expected = "/usr/local/bin/ffmpeg"
        val actual = testWhicher.which("ffmpeg")
        assertEquals(expected, actual)
    }

    @Test
    fun allWhiches() {
        val expected = arrayOf("/usr/local/bin/gcc", "/home/foo/bin/gcc")
        val actual = testWhicher.allWhiches("gcc")
        assertTrue(expected contentEquals actual)
    }

    @Test
    fun silentWhich() {
        assertTrue(testWhicher.silentWhich("ffmpeg"))
        assertTrue(testWhicher.silentWhich("gcc"))
    }

    @Test
    fun whichNotFound() {
        assertNull(testWhicher.which("gm"))
        assertTrue(testWhicher.allWhiches("gm").isEmpty())
        assertFalse(testWhicher.silentWhich("gm"))
    }
}
