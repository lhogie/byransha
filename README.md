

# byransha
Byransha is a framework for Web applications which relies on 
the concept of "graph" everywhere it proves relevant.
In particular, within Byransha, graphs are used to organize both technical and business data, 
to implement data persistence, and to drive the workflow of the user interface.

Byransha consists of:
- a Java backend exposing a set of JSON services
- a Web (TypeScript/React.js) frontend

The first use case and initial motivation for this project is to provide the I3S laboratory
with an human resources dashboard.

# Configuration
## Development frontend
To run the frontend locally in development mode, ensure you have [Bun](https://bun.sh) installed.

### Install Dependencies
Navigate to the frontend directory  `~\byransha\src\main\java\frontend` and run:
```bash
bun install
```

### First start the backend
Run class `byransha.web.WebServer`.

### Then start the Frontend
In the same frontend directory, execute:
```bash
bun start
```

If successful, you should see an icon in the bottom-right corner of your screen, which can be clicked.
When you make changes to the frontend directory, the server will automatically reload. You can view the updates in your browser without needing to refresh the page.

# files on server
# continuous integration


# Former students

Lamyae Fakir
Quentin Nicolini
Mathéo BALAZUC
CELESTE Maxime
Matthias Carré
EL BAZZAL Nour
Mourad HADDOUDI

# Current students

Kishan Turpin
