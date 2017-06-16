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

class Whicher(val path: Array<String>, val resolver: SystemResolver) {
    companion object {
        val systemResolver: SystemResolver =
            if (System.getProperty("os.name").contains("windows", true))
                WindowsResolver()
            else
                UnixResolver()

        private val system = Whicher()

        fun which(program: String) = system.which(program)
    }

    constructor(path: Array<String>) : this(path, systemResolver)
    constructor(path: String, resolver: SystemResolver = systemResolver) : this(resolver.splitPath(path), resolver) {}
    constructor(resolver: SystemResolver = systemResolver) : this(System.getenv("PATH"), resolver) {}

    fun which(program: String) = path.flatMap {
        resolver.resolve(it, program)
    }.find {
        resolver.canExecute(it)
    }

    fun allWhiches(program: String) = path.flatMap {
        resolver.resolve(it, program)
    }.filter {
        resolver.canExecute(it)
    }.toTypedArray()

    fun silentWhich(program: String) = path.flatMap {
        resolver.resolve(it, program)
    }.any {
        resolver.canExecute(it)
    }
}
