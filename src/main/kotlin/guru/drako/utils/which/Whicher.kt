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

import java.nio.file.Path
import java.nio.file.Paths

class Whicher(val path: Array<Path>, val resolver: SystemResolver) {
    companion object {
        private val systemResolver: SystemResolver =
            if (java.lang.System.getProperty("os.name").contains("windows", true))
                WindowsResolver()
            else
                UnixResolver()

        @JvmField
        val system = Whicher()

        fun which(program: Path) = system.which(program)
        fun which(program: String) = system.which(program)

        fun allWhiches(program: Path) = system.allWhiches(program)
        fun allWhiches(program: String) = system.allWhiches(program)

        fun silentWhich(program: Path) = system.silentWhich(program)
        fun silentWhich(program: String) = system.silentWhich(program)
    }

    constructor(path: Array<Path>) : this(path, systemResolver)
    constructor(path: String, resolver: SystemResolver = systemResolver) : this(resolver.splitPath(path), resolver) {}
    constructor(resolver: SystemResolver = systemResolver) : this(System.getenv("PATH"), resolver) {}

    fun which(program: Path) = path.flatMap {
        resolver.resolve(it, program)
    }.find {
        resolver.canExecute(it)
    }
    fun which(program: String) = which(Paths.get(program))

    fun allWhiches(program: Path) = path.flatMap {
        resolver.resolve(it, program)
    }.filter {
        resolver.canExecute(it)
    }.toTypedArray()
    fun allWhiches(program: String) = allWhiches(Paths.get(program))

    fun silentWhich(program: Path) = path.flatMap {
        resolver.resolve(it, program)
    }.any {
        resolver.canExecute(it)
    }
    fun silentWhich(program: String) = silentWhich(Paths.get(program))
}
