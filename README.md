# Ricart-Agrawala Algorithm

## What is this?
This is an implementation of Ricart-Agrawala Algorithm in Java, using JGroups

### What is working?
* Send messages to cluster (request method)
* Receive messages (receive method). Receives PeerInfo object
* LogicalClock synchronization
* Replies
* Queues and State updates
* After leaving CriticalSection reply to all Requests by the lowest logicalClock
* Graphic Interface with real time updates
* 
_Ricart-Agrawala Algorithm diagram_

![RicartAgrawalaAlgorithmDiagram](https://www.cs.rutgers.edu/~pxk/417/notes/images/mutex-ricart.png)

Feel free to contribute or use this as you want!
