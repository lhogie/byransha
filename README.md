

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


# Developer Setup

## Prerequisites

The project requires:
- **Java 23+** ([Download](https://adoptium.net/))
- **Maven 3.9+** ([Download](https://maven.apache.org/download.cgi))
- **Node.js 20+** ([Download](https://nodejs.org/))
- **Bun** ([Download](https://bun.sh))

### Check Requirements

A script is provided to verify all prerequisites are installed:

**Linux/Mac:**
```bash
bash checkRequirements.sh
```

**Windows:**
```bash
# Install Git Bash first: https://git-scm.com/downloads/win
bash checkRequirements.sh
```

The script will automatically detect missing tools and provide installation instructions.

## Quick Start

### Option : Using IntelliJ IDEA

1. Open the project in IntelliJ IDEA
2. A shared run configuration **"Start Backend"** is available in the toolbar
3. For the frontend, navigate to `src/main/java/frontend/` and run:
   ```bash
   bun install
   bun start
   ```

### Option 2: Manual Setup

**Backend:**
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass=byransha.web.WebServer
```

**Frontend:**
```bash
cd src/main/java/frontend
bun install
bun start
```

## Development

- **Backend** runs on `http://localhost:8080`
- **Frontend** runs on `http://localhost:3000` (or another port if 3000 is busy)
- Frontend has hot-reload: changes are reflected automatically, no browser refresh needed



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
