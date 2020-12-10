# Advent of Weirdness 2020

When trying to solve Day 7's problem with a depth first approach, I
was consistently computing the wrong answer (`30449`). The computation
worked fine for all test cases I could create, as well as the example
data.

I tried a different, breadth-first approach, and was able to compute
the correct answer (`30899`).

On a whim, I switched the DFS version to count using `BigInt`s (rather
than `Long`s), on the offchance that there was some kind of overflow
error.

This fixed the error. However, the numbers weren't nearly so large
that it would have been an overflow problem, so I just assumed it was
a bug in the `scala-native` implementation.

I decided to run the script using the JVM scala...and this time both
the `Long` and `BigInt` versions of the DFS were wrong (version
`2.11.12`, FWIW the same version used by scala-native).

Finally, I ran the script against the "frontpage" JVM scala
(`2.13.4`), and it had the same results.

| Method | Type   | Scala Version | Answer |
| ------ | ------ | ------------- | ------ |
| DFS    | Long   | scala-native  | 30449  |
| DFS    | BigInt | scala-native  | 30899  |
| DFS    | Long   | `2.11.12`     | 30449  |
| DFS    | BigInt | `2.11.12`     | 30449  |
| DFS    | Long   | `2.13.4`      | 30449  |
| DFS    | BigInt | `2.13.4`      | 30449  |

So the only correct answer produced via my depth-first-search is the
scala-native `BigInt` one.

The breadth-first-search computed the correct answer across all
systems & versions.

## Results

Output of `sbt run` for all three projects.
* The breadth-first "Long" and "BigInt" versions produce the correct answer across all three
* The depth-first "Long" version produces a wrong answer for all three (scala-native & scala-jvm)

Interestingly:
* The depth-first "BigInt" version produces the correct answer for `scala-native`
* The depth-first "BigInt" version produces an incorrect answer for `scala-2.11.12` & `scala-2.13.4`
```
    [chandler day7-weird]$ for d in scala-*; do (cd $d; echo $d; sbt run); done
    scala-2.11.12
    [info] welcome to sbt 1.3.13 (Oracle Corporation Java 1.8.0_265)
    [info] loading settings for project scala-2-11-12-build from plugins.sbt ...
    [info] loading project definition from /home/chandler/code/advent/day7-weird/scala-2.11.12/project
    [info] loading settings for project scala-2-11-12 from build.sbt ...
    [info] set current project to scala-2-11-12 (in build file:/home/chandler/code/advent/day7-weird/scala-2.11.12/)
    [info] Compiling 1 Scala source to /home/chandler/code/advent/day7-weird/scala-2.11.12/target/scala-2.11/classes ...
    [info] running Main 
    (DFS Long,30449)
    (DFS BigInt,30449)
    (BFS Long,30899)
    (BFS BigInt,30899)
    [success] Total time: 6 s, completed Dec 10, 2020 12:22:14 PM
    scala-2.13.4
    [info] welcome to sbt 1.3.13 (Oracle Corporation Java 1.8.0_265)
    [info] loading settings for project scala-2-13-4-build from plugins.sbt ...
    [info] loading project definition from /home/chandler/code/advent/day7-weird/scala-2.13.4/project
    [info] loading settings for project scala-2-13-4 from build.sbt ...
    [info] set current project to scala-2-13-4 (in build file:/home/chandler/code/advent/day7-weird/scala-2.13.4/)
    [info] running Main 
    (DFS Long,30449)
    (DFS BigInt,30449)
    (BFS Long,30899)
    (BFS BigInt,30899)
    [success] Total time: 2 s, completed Dec 10, 2020 12:22:23 PM
    scala-native
    [info] welcome to sbt 1.3.13 (Oracle Corporation Java 1.8.0_265)
    [info] loading settings for project scala-native-build from plugins.sbt ...
    [info] loading project definition from /home/chandler/code/advent/day7-weird/scala-native/project
    [info] loading settings for project scala-native from build.sbt ...
    [info] set current project to scala-native (in build file:/home/chandler/code/advent/day7-weird/scala-native/)
    [info] Linking (1359 ms)
    [info] Discovered 1044 classes and 7816 methods
    [info] Optimizing (debug mode) (3551 ms)
    [info] Generating intermediate code (3417 ms)
    [info] Produced 4 files
    [info] Compiling to native code (1311 ms)
    [info] Linking native code (immix gc, none lto) (161 ms)
    [info] Total (9946 ms)
    (DFS Long,30449)
    (DFS BigInt,30899)
    (BFS Long,30899)
    (BFS BigInt,30899)
    [success] Total time: 13 s, completed Dec 10, 2020 12:22:44 PM
```    

The source code for all three projects is the same, the only difference is in `build.sbt` and `project/plugins.sbt`:

```
[chandler day7-weird]$ cksum scala*/Main.scala
2273993435 3243 scala-2.11.12/Main.scala
2273993435 3243 scala-2.13.4/Main.scala
2273993435 3243 scala-native/Main.scala
```
