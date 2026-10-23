# Java Maze Pathfinding

A maze generation and pathfinding system written in Java, built in three stages over a
semester for the ATP course at Ben-Gurion University. It begins as a pure algorithms
library, grows into a client-server application, and ends as a JavaFX desktop game. Built
together with a course partner.

> The three stages live on separate branches, so each one can be read on its own:
> [`master`](../../tree/master) (Part A), [`partB`](../../tree/partB), and
> [`partC`](../../tree/partC).

## Part A — Algorithms (`master`)

The foundation: maze generation and graph search behind clean abstract interfaces.

- **Generators** — an empty generator, a simple random generator, and `MyMazeGenerator`,
  which carves a solvable maze using a randomized depth-first (backtracker) approach and
  guarantees reachable start and goal positions.
- **Search** — Breadth-First Search, Depth-First Search, and Best-First Search, all sharing
  a common `ASearchingAlgorithm` base and returning a reconstructed solution path. A
  `SearchableMaze` adapter exposes a maze to the generic search layer.
- **Tests** — JUnit coverage for the generators and the search algorithms, including null
  and invalid-input handling.

## Part B — Client-Server (`partB`)

The algorithms become a networked service.

- A configurable, **thread-pooled server** that handles multiple clients concurrently,
  with two strategies: generate a maze and solve a given maze.
- A **client** that talks to the server over sockets using a pluggable client strategy.
- Two **compression streams** for sending mazes over the network: a run-length encoder
  (`SimpleCompressorOutputStream`) and a DEFLATE-based one (`MyCompressorOutputStream`),
  with matching decompressors and JUnit tests.
- Runtime configuration (thread-pool size, generator and solver choice) via a properties
  file.

## Part C — JavaFX Desktop Game (`partC`)

A graphical client built on a clean **MVVM** architecture (Model, View, ViewModel) and
packaged with Maven.

- A playable maze game: generate a maze, move a character from start to goal, and request
  an automatic solution.
- Model, View and ViewModel are decoupled, with the view driven by property bindings.
- Audio, images and CSS styling, and a Maven build (`pom.xml`, Maven wrapper).

## Tech

Java · JUnit · Maven · JavaFX · sockets · DEFLATE / RLE compression · MVVM

## Running it

Each branch is a self-contained stage. Check out the branch you want
(`git checkout partB`), then compile and run from `src`. Part C builds and runs with Maven
(`mvn javafx:run`).
