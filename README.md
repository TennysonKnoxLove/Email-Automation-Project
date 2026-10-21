# Email Automation

Desktop app for registering an account, keeping contacts, drafting a message with the OpenAI API, and sending it over SMTP.

Run every command below from the project root (the directory that contains `pom.xml`).

## What you need

- JDK 17 or newer. Check with `java -version`.
- MySQL listening on `localhost` port 3306.
- The MySQL account `root` with an empty password. Those credentials are set in `src/main/java/Backend/Utils/DB.java`. On startup the app creates a database named `users` and the `user` and `contacts` tables from `src/main/resources/Schema/user.sql` and `src/main/resources/Schema/contacts.sql`.

## Set the environment variables

The app reads the process environment with `System.getenv`. It does not open a `.env` file. Create `.env` yourself in the project root, using the names in `.env.example`, and fill in your own values. `.env` is listed in `.gitignore`, so those secrets are not committed.

```
OPENAI_API_KEY="sk-..."
SMTP_FROM="you@example.com"
SMTP_PASSWORD="your-mailbox-password"
SMTP_HOST="smtp.gmail.com"
```

In the same terminal you will use to start the app, load that file into the environment:

```bash
set -a
source .env
set +a
```

`set -a` exports every assignment `source` reads, which is what `System.getenv` sees. A variable loaded in another terminal, or after the app is already open, is invisible to this process. If you start the app from an IDE, put the same four names on that run configuration. The IDE does not load `.env` on its own.

| Name | Purpose |
| --- | --- |
| `OPENAI_API_KEY` | Bearer token for `https://api.openai.com/v1/chat/completions` when a message is drafted |
| `SMTP_FROM` | Mailbox address used to sign in and send |
| `SMTP_PASSWORD` | Password for that mailbox |
| `SMTP_HOST` | SMTP hostname. The app uses port 587 with STARTTLS |

Startup does not check these. They are read when a message is drafted or sent. A missing one fails that action with `Set <NAME> in the environment.`

## Compile and run

`pom.xml` pins the libraries. The Maven Wrapper downloads Maven, then those libraries, into your local cache (`~/.m2`). Class files go in `target/`, which is gitignored.

With the environment loaded in this terminal:

```bash
./mvnw compile exec:java
```

On Windows: `mvnw.cmd compile exec:java`

That builds `src/main/java` and starts `Main`. The libraries are:

- MySQL Connector/J 9.1.0
- Protocol Buffers, which that driver requires
- Gson 2.11.0
- JavaMail 1.6.2
- JavaBeans Activation Framework 1.1.1, which JavaMail needs on JDK 17 and later
