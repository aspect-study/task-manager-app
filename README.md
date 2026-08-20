# task-manager-app

A Vaadin 24 + Spring Boot 3 learning project. The eventual goal is a task manager; right now it's a working scaffold with one demo view, used to explore Vaadin Flow layouts, theming, and how Tailwind behaves inside a Web Components UI.

**Current state: scaffold.** There is no task domain yet — no entity, repository, service, or CRUD. `HomeView` is a `VerticalLayout` exercise, not a task list. Read the [Current state](#current-state) section before cloning if you're expecting a working app.

---

## Stack

| Layer | Choice |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.3 |
| UI | Vaadin 24.8.2 (Flow — server-side Java UI, no separate frontend app) |
| Persistence | Spring Data JPA + Hibernate |
| Database | MySQL 8 (`mysql-connector-j`, runtime scope) |
| Validation | `spring-boot-starter-validation` |
| Boilerplate | Lombok (annotation processor configured) |
| Frontend build | Vite 6 + npm, driven automatically by Vaadin |
| Styling | Vaadin Lumo, plus a Tailwind CSS v3 utility layer |
| Build | Maven, wrapper included (`mvnw` / `mvnw.cmd`) |
| Testing | JUnit 5 via `spring-boot-starter-test` |

Vaadin Flow means the UI is written in Java. `HomeView` composes `H1`, `Paragraph`, and `Button` objects server-side; Vaadin syncs them to the browser over a websocket. The `src/main/frontend/generated/` tree, `vite.config.ts`, `types.d.ts`, `tsconfig.json`, and `package.json` are all produced by Vaadin — they aren't hand-written.

---

## Prerequisites

- JDK 21
- MySQL 8 listening on **port 3307** (not the default 3306)
- Node.js — optional. Vaadin downloads its own Node if one isn't found.

---

## Setup

### 1. Create the database

The app will not start without it. `spring-boot-starter-data-jpa` is on the classpath and `application.properties` points at a datasource, so Spring initializes a connection pool at startup and fails hard if the database is unreachable.

```sql
CREATE DATABASE task_management;
```

There are no tables to create — there are no entities yet.

### 2. Check the connection settings

`src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/task_management
spring.datasource.username=root
spring.datasource.password=root
```

Change the port to `3306` if that's your setup, and adjust the credentials.

### 3. Run

```bash
./mvnw spring-boot:run
```

First run takes a while — Vaadin installs frontend dependencies and builds the dev bundle. The app opens a browser automatically (`vaadin.launch-browser=true`) at `http://localhost:8080`.

DevTools live reload is enabled, so Java changes trigger a restart and Vaadin hot-reloads the frontend.

### 4. Production build

```bash
./mvnw clean package -Pproduction
java -jar target/task-manager-app-0.0.1-SNAPSHOT.jar
```

The `production` profile runs Vaadin's `build-frontend` goal to produce an optimized bundle and excludes `vaadin-dev` (the in-browser Copilot tooling) from the artifact.

---

## Project layout

```
src/main/java/com/aspect/vaadin/study/task_manager_app/
├── TaskManagerAppApplication.java    Entry point, implements AppShellConfigurator
└── views/
    └── HomeView.java                 @Route("") — the only view

src/main/resources/
└── application.properties            DevTools + datasource config

src/main/frontend/
├── styles/styles.css                 Tailwind source (@tailwind directives + .my-bg)
├── styles/output.css                 Compiled Tailwind output, imported by HomeView
├── index.html                        Vaadin-generated shell
├── test.html                         Scratch file
└── generated/                        Vaadin-generated — do not edit

src/main/bundles/dev.bundle           Vaadin's prebuilt dev-mode frontend bundle
```

---

## Styling approach

Two systems are in play. Vaadin ships Lumo, its own design system, applied by default. On top of that, `styles.css` defines a Tailwind layer:

```css
@tailwind base;
@tailwind components;
@tailwind utilities;

.my-bg {
  @apply bg-blue-100 p-4;
}
```

`HomeView` pulls the compiled result in with `@CssImport("./styles/output.css")` and applies `my-bg` to the root layout.

Two scripts exist for rebuilding it:

```bash
npm run tailwind:build    # one-shot
npm run tailwind:watch    # watch mode
```

Neither works from a fresh clone — see the gaps below.

---

## Current state

What exists:

- Spring Boot application boots, Vaadin serves a view at `/`
- One route (`@Route("")` → `HomeView`) demonstrating `VerticalLayout` alignment, `setAlignSelf`, inline styling, and a click listener that mutates button text
- Maven build with a working production profile
- A context-load smoke test

What doesn't exist yet:

- No `Task` entity, no repository, no service layer
- No CRUD — nothing is persisted, nothing is read
- No Grid, form, or form binder
- No security, no users, no login
- No database schema or migration tooling
- No routing beyond the single root view

A reasonable next order of work would be: `Task` entity → `TaskRepository` → service layer with validation → a `Grid` view with a `Binder`-backed form → Flyway for schema versioning → then security if it needs to be multi-user.

---

## Known gaps

Recording these so they don't get rediscovered later:

**`npm run tailwind:build` fails on a fresh clone.** `tailwindcss` isn't listed in `package.json` devDependencies, and there's no `tailwind.config.js` in the repo. The committed `output.css` was generated by Tailwind v3.4.1 from some external install. Without the config, a rebuild has no content paths to scan and will strip every utility class. Fix: add `tailwindcss@^3` as a devDependency and commit a config with `content` pointing at `src/main/java/**/*.java` and `src/main/frontend/**/*.{html,css}`.

**Tailwind can't reach inside Vaadin components.** Vaadin components are Web Components with Shadow DOM, and Tailwind utility classes don't cross shadow boundaries. `my-bg` works because it's applied to a host element. The moment you try to style the inside of a `Grid`, `TextField`, or `Button`, Tailwind will do nothing and you'll need Lumo custom properties or `::part()` selectors instead. Worth deciding early whether Tailwind earns its place here or whether Lumo theming alone is simpler.

**Vaadin-generated files are committed.** `src/main/frontend/generated/` is 7.2 MB and `src/main/bundles/dev.bundle` is another 2.3 MB — about 10 MB of regenerable artifacts wrapping roughly 100 lines of hand-written Java. Vaadin rebuilds all of it. Standard Vaadin projects gitignore `src/main/frontend/generated/`. Note that removing them now only stops future growth; the blobs stay in history unless the history is rewritten.

**`@Theme` is imported but never used** in `TaskManagerAppApplication.java`. Either annotate the class with `@Theme("...")` and add a theme folder, or drop the import.

**Database credentials are hardcoded** as `root`/`root` in `application.properties`. Fine for a local study project, but the habit is worth breaking early — `${DB_PASSWORD}` with an env var costs nothing to set up now.

**No `spring.jpa.hibernate.ddl-auto` is set.** Boot's default for a non-embedded database is `none`, so Hibernate won't create anything. That's harmless while there are no entities, but the first entity added will silently have no table until this is set or Flyway is introduced. Flyway is the better answer.

**`test.html` is a scratch file** with a `<div>` inside `<head>` — invalid HTML, and unreferenced by anything. Safe to delete.

**Placeholder metadata in `pom.xml`.** The description still reads "Demo project for Spring Boot", and the empty `<licenses>`, `<developers>`, and `<scm>` blocks are Spring Initializr leftovers that can trip up Maven goals expecting populated values. Fill them in or remove them.

---

## License

None specified. `package.json` says `UNLICENSED`, which is Vaadin's generated default rather than a deliberate choice.
