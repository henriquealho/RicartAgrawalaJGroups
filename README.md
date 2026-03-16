# Ricart-Agrawala Algorithm with JGroups

A Java implementation of the **Ricart-Agrawala** distributed mutual exclusion algorithm using the [JGroups](http://www.jgroups.org/) cluster communication toolkit. This project demonstrates how multiple networked processes can safely coordinate access to a critical section without a central coordinator.

---

## Table of Contents

- [About the Algorithm](#about-the-algorithm)
- [Features](#features)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Running the Application](#running-the-application)
- [How It Works](#how-it-works)
- [Dependencies](#dependencies)
- [Contributing](#contributing)

---

## About the Algorithm

The **Ricart-Agrawala Algorithm** (1981) is a classic solution to the distributed mutual exclusion problem. It allows a set of processes in a distributed system to mutually exclude each other from a shared critical section using only message passing — no central lock server is required.

### Key Concepts

- **Logical Clocks (Lamport Clocks)**: Each peer maintains a logical clock that is incremented on every event and synchronized on every received message. This provides a consistent ordering of events across the distributed system.
- **Request Phase**: When a peer wants to enter the critical section, it broadcasts a `REQUEST` message (containing its logical clock value and address) to all other peers in the cluster.
- **Reply Phase**: Each peer that receives a `REQUEST` either replies immediately (if it does not want the critical section or has a lower-priority request) or defers its reply until after it exits the critical section.
- **Priority Rule**: If two peers request the critical section simultaneously, the one with the **lower logical clock value** gets priority. In case of a tie, the peer with the lower address wins.
- **Critical Section Entry**: A peer may enter the critical section only after receiving replies from **all** other peers in the cluster.

_Ricart-Agrawala Algorithm diagram_

![RicartAgrawalaAlgorithmDiagram](https://www.cs.rutgers.edu/~pxk/417/notes/images/mutex-ricart.png)

---

## Features

- **Distributed mutual exclusion** — safe concurrent access to a critical section across multiple JVM instances
- **Lamport logical clock synchronization** — consistent event ordering without relying on physical clocks
- **Automatic request queuing** — deferred replies are queued and sent after the critical section is released
- **JGroups-based cluster membership** — peers join and leave the cluster dynamically; the cluster view updates automatically
- **Swing GUI** — real-time graphical interface showing each peer's address, GUID, state, and logical clock value
- **Interactive request button** — trigger a critical section entry request with a single click; the button is disabled while waiting or inside the critical section

---

## Project Structure

```
RicartAgrawalaJGroups/
├── src/
│   └── pt/ipb/sd/
│       ├── Peer.java        # Core algorithm logic (cluster communication, state machine, message handling)
│       ├── PeerGUI.java     # Swing GUI — extends Peer; displays real-time peer state
│       └── PeerInfo.java    # Serializable data model carrying logical clock, GUID, and address
├── jgroups-3.6.7.Final.jar  # JGroups cluster communication library
├── jgoodies-forms-1.8.0.jar # JGoodies Forms layout library (used by the GUI)
└── README.md
```

### Class Overview

| Class | Responsibility |
|---|---|
| `Peer` | Extends `ReceiverAdapter`; manages the JChannel, sends/receives messages, maintains state (`ready` / `waiting` / `inCriticalSection`), and handles logical clock updates. |
| `PeerGUI` | Extends `Peer`; builds the Swing interface and overrides `refresh()` to update displayed values on each state change. |
| `PeerInfo` | Serializable value object transmitted between peers; carries `logicalClock`, `guid`, and `address`. |

---

## Prerequisites

- **Java 8** or later
- The following JAR files (included in the repository root):
  - `jgroups-3.6.7.Final.jar`
  - `jgoodies-forms-1.8.0.jar`

No build tool (Maven, Gradle) is required — compile and run with standard `javac`/`java` commands.

---

## Running the Application

### 1. Compile

```bash
javac -cp "jgroups-3.6.7.Final.jar:jgoodies-forms-1.8.0.jar" \
      -d out \
      src/pt/ipb/sd/*.java
```

> On Windows, replace `:` with `;` in the classpath.

### 2. Run a peer (GUI mode)

```bash
java -cp "out:jgroups-3.6.7.Final.jar:jgoodies-forms-1.8.0.jar" \
     pt.ipb.sd.PeerGUI
```

Start **multiple instances** (on the same machine or across a network) to form a cluster. Each instance will automatically discover and join the same JGroups cluster.

### 3. Request the critical section

Click the **REQUEST** button in any peer window. That peer will:
1. Broadcast a request to all other peers.
2. Wait until all peers have replied.
3. Enter the critical section (simulated as a 5-second pause).
4. Release the critical section and send any deferred replies.

---

## How It Works

```
Peer A                         Peer B                        Peer C
  |                               |                              |
  |-- REQUEST(clock=1) ---------> |                              |
  |-- REQUEST(clock=1) ---------------------------------> |
  |                               |                              |
  |                    REPLY <--- |  (B is not requesting)       |
  |                               |              REPLY <-------- |  (C is not requesting)
  |                               |                              |
  | [enters critical section]     |                              |
  |                               |                              |
  | [exits critical section]      |                              |
  |                               |                              |
```

1. **State machine** — each peer is in one of three states:
   - `ready` — idle, will immediately reply to any incoming request
   - `waiting` — has sent its own request and is collecting replies
   - `inCriticalSection` — currently executing the critical section
2. **Receiving a request** — if the peer is `ready`, it replies immediately. If it is `waiting` or `inCriticalSection` and its own request has higher priority, it defers the reply by adding it to the acknowledge queue.
3. **Exiting the critical section** — all deferred replies in the acknowledge queue are sent, and the peer returns to the `ready` state.

---

## Dependencies

| Library | Version | Purpose |
|---|---|---|
| [JGroups](http://www.jgroups.org/) | 3.6.7.Final | Reliable group communication and cluster membership |
| [JGoodies Forms](https://www.jgoodies.com/freeware/libraries/forms/) | 1.8.0 | Swing layout manager used by the GUI |

---

## Contributing

Contributions, bug reports, and suggestions are welcome! Feel free to open an issue or submit a pull request.

Please follow the existing code style and include a brief description of your changes.
