

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


## frontend

# Configuration for developers
If you plan to develop Byransha and run it locally, you'll need [Bun](https://bun.sh).
Then go to the frontend directory  `~\byransha\src\main\java\frontend` and install dependencies
by running:
```bash
bun install
```
Then run the TypeScript on-the-fly compiler:
```bash
bun start
```

Run class `byransha.web.WebServer` to start the backend.

When you make changes to the frontend directory, the server will automatically reload. You can view the updates in your browser without needing to refresh the page.


# Team
- Luc Hogie (CNRS Research Engineer)
- Kishan Turpin (Master 2 student)
- Quentin Nicolini (Master 1 student)

## Former students

- Lamyae Fakir
- Mathéo BALAZUC
- CELESTE Maxime
- Matthias Carré
- EL BAZZAL Nour
- Mourad HADDOUDI
