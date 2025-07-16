

# Byransha
Byransha is a framework for Web applications which relies on 
the concept of "graph" everywhere it proves relevant.
In particular, within Byransha, graphs are used to organize both technical and business data, 
to implement data persistence, and to drive the workflow of the user interface.

Byransha consists of:
- a Java backend exposing a set of JSON services
- a Web (TypeScript/React.js) frontend

The first use case and initial motivation for this project is to provide the I3S laboratory
with an human resources dashboard.

## the backend
The backend is a Java process which exposes a graph of both technical and business nodes in a graph.
The graph is accessible from external processes via a set of JSON Web-services.
Each web service is defined to apply to a certain class of nodes.

### data persistence
We have defined a mapping between the graph in-memory and the tree projection on disk. 


## the frontend
The frontend is written in TypeScript and relies on React.js.
It defines that the user is working on a single node at a time. On that node, many endpoints can be executed, some of
them providing information of the node, so as to feed a *view* on the frontend.
The frontend is then organized as a grid of such views for the _current node_.
There exist technical views and business views.


# Configuration for developers
If you plan to develop Byransha and run it locally, you'll need the [Bun](https://bun.sh) on-the-fly compiler.
Go to the frontend source directory  `~\byransha\src\main\java\frontend` to:
- install dependencies:
```bash
bun install
```
- run it:
```bash
bun start
```
When you make changes to the frontend directory, [bun] recompiles automatically  and you can view the updates in your browser. No refresh needed.

Run class `byransha.web.WebServer` to start the backend.



# Team
- Luc Hogie (CNRS Research Engineer)
- Kishan Turpin (Master 2 student)
- Quentin Nicolini (Master 1 student)

## former members

- Lamyae Fakir
- Mathéo BALAZUC
- CELESTE Maxime
- Matthias Carré
- EL BAZZAL Nour
- Mourad HADDOUDI
