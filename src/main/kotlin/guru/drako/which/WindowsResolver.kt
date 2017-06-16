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

import java.nio.file.Paths

/**
 * Whicher backend for Windows systems.
 */
class WindowsResolver: SystemResolver {
    /**
     * Splits the given PATH variable into its parts.
     * Uses the Windows delimiter ";".
     *
     * @param path A string in the format of the PATH environment variable.
     * @return An array of path strings.
     */
    override fun splitPath(path: String) = path.split(';').toTypedArray()

    /**
     * Concatenate <code>dir</code> and <code>file</code> and resolve them to an absolute path.
     * This does not check for the existence of the file.
     *
     * @param dir The directory in which the file is located.
     * @param file The name of the file.
     * @return The absolute path to the file including the filename.
     *
     * @todo This should return more than one path (e.g. for foo it should return foo.exe, foo.bat etc.)
     */
    override fun resolve(dir: String, file: String) = listOf(Paths.get(dir).resolve(file).toAbsolutePath().toString())

    /**
     * Check if the given filename is executable.
     *
     * @param filename The absolute path to a file.
     * @return <code>true</code> if the file exists and is executable. <code>false</code> otherwise.
     */
    override fun canExecute(filename: String) = Paths.get(filename).toFile().canExecute()
}
