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

import com.github.salomonbrys.kodein.*
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Class to locate programs in the user's path.
 *
 * @property kodein A DI container.
 * @property path An array of paths to search for programs.
 * @property resolver The resolver to use.
 */
class Whicher(override val kodein: Kodein) : KodeinAware {
    val resolver: SystemResolver = instance()
    val path: Array<Path> = with(kodein).instance("path_arr")

    /**
     * Static access to the default Whicher.
     *
     * The default Which uses the default SystemResolver and the PATH environment variable.
     */
    companion object {
        internal val kodein = Kodein {
            bind<SystemResolver>() with singleton {
                if (System.getProperty("os.name").contains("windows", true))
                    WindowsResolver()
                else
                    UnixResolver()
            }
            bind<String>("path_env") with provider { System.getenv("PATH") }
            bind<Array<Path>>("path_arr") with factory {
                kodein: Kodein -> kodein
                    .instance<SystemResolver>()
                    .splitPath(kodein.provider<String>("path_env")())
            }
        }

        /**
         * This allows Java to access the default Whicher through the static field Whicher.system.
         */
        @JvmField
        val system = Whicher(kodein)

        /**
         * Calls which(program: Path) on the default Whicher instance.
         */
        fun which(program: Path) = system.which(program)

        /**
         * Calls which(program: String) on the default Whicher instance.
         */
        fun which(program: String) = system.which(program)

        /**
         * Calls allWhiches(program: Path) on the default Whicher instance.
         */
        fun allWhiches(program: Path) = system.allWhiches(program)

        /**
         * Calls allWhiches(program: String) on the default Whicher instance.
         */
        fun allWhiches(program: String) = system.allWhiches(program)

        /**
         * Calls silentWhich(program: Path) on the default Whicher instance.
         */
        fun silentWhich(program: Path) = system.silentWhich(program)

        /**
         * Calls silentWhich(program: String) on the default Whicher instance.
         */
        fun silentWhich(program: String) = system.silentWhich(program)
    }

    /**
     * Looks for <code>program</code> in <code>path</code> and returns the first found instance.
     *
     * @param program The program to look for.
     * @return The found program or <code>null</code>.
     */
    fun which(program: Path) = path.flatMap {
        resolver.resolve(it, program)
    }.find {
        resolver.canExecute(it)
    }

    /**
     * Looks for <code>program</code> in <code>path</code> and returns the first found instance.
     *
     * @param program The program to look for.
     * @return The found program or <code>null</code>.
     */
    fun which(program: String) = which(Paths.get(program))

    /**
     * Looks for <code>program</code> in <code>path</code> and returns all found instances.
     *
     * It corresponds to the -a option of the which tool.
     *
     * @param program The program to look for.
     * @return An array of found programs.
     */
    fun allWhiches(program: Path) = path.flatMap {
        resolver.resolve(it, program)
    }.filter {
        resolver.canExecute(it)
    }.toTypedArray()

    /**
     * Looks for <code>program</code> in <code>path</code> and returns all found instances.
     *
     * It corresponds to the -a option of the which tool.
     *
     * @param program The program to look for.
     * @return An array of found programs.
     */
    fun allWhiches(program: String) = allWhiches(Paths.get(program))

    /**
     * Looks for <code>program</code> in <code>path</code> and returns whether an instance was found.
     *
     * It corresponds to the -s option of the which tool.
     *
     * @param program The program to look for.
     * @return <code>true</code> in case the program was found. <code>false</code> otherwise.
     */
    fun silentWhich(program: Path) = path.flatMap {
        resolver.resolve(it, program)
    }.any {
        resolver.canExecute(it)
    }

    /**
     * Looks for <code>program</code> in <code>path</code> and returns whether an instance was found.
     *
     * It corresponds to the -s option of the which tool.
     *
     * @param program The program to look for.
     * @return <code>true</code> in case the program was found. <code>false</code> otherwise.
     */
    fun silentWhich(program: String) = silentWhich(Paths.get(program))
}
