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

package guru.drako.which;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

public class WhicherJavaTest {
    static class TestResolver implements SystemResolver {
        @NotNull
        @Override
        public Path[] splitPath(@NotNull String path) {
            final String[] splitted = path.split(":");
            return Arrays.stream(splitted).map(p -> Paths.get(p)).toArray(Path[]::new);
        }

        @NotNull
        @Override
        public List<Path> resolve(@NotNull Path dir, @NotNull Path file) {
            return Stream.of(dir.resolve(file)).collect(Collectors.toList());
        }

        @Override
        public boolean canExecute(@NotNull Path file) {
            final String[] existing = {
                "/usr/local/bin/ffmpeg",
                "/usr/local/bin/gcc",
                "/home/foo/bin/gcc"
            };
            return Arrays.asList(existing).contains(file.toString());
        }
    }

    private static final Whicher testWhicher = new Whicher("/usr/bin:/usr/local/bin:/home/foo/bin", new TestResolver());

    @Test
    public void split() {
        final Path[] expected = {
            Paths.get("/usr/bin"),
            Paths.get("/usr/local/bin"),
            Paths.get("/home/foo/bin")
        };
        final Path[] actual = testWhicher.getPath();
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void which() {
        final Path expected = Paths.get("/usr/local/bin/ffmpeg");
        final Path actual = testWhicher.which("ffmpeg");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void allWhiches() {
        final Path[] expected = {
            Paths.get("/usr/local/bin/gcc"),
            Paths.get("/home/foo/bin/gcc")
        };
        final Path[] actual = testWhicher.allWhiches("gcc");
        Assert.assertArrayEquals(expected, actual);
    }

    @Test
    public void silentWhich() {
        Assert.assertTrue(testWhicher.silentWhich("ffmpeg"));
        Assert.assertTrue(testWhicher.silentWhich("gcc"));
    }

    @Test
    public void whichNotFound() {
        Assert.assertNull(testWhicher.which("gm"));
        Assert.assertTrue(testWhicher.allWhiches("gm").length == 0);
        Assert.assertFalse(testWhicher.silentWhich("gm"));
    }
}
