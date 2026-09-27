require("dotenv").config();

const express = require("express");
const session = require("express-session");
const MySQLStore = require("express-mysql-session")(session);

const passport = require("./auth");
const { initDb } = require("./db");
const authRoutes = require("./routes/auth");
const todoRoutes = require("./routes/todos");

const app = express();

const sessionStore = new MySQLStore({
  host: process.env.MYSQL_HOST,
  port: Number(process.env.MYSQL_PORT),
  database: process.env.MYSQL_DATABASE,
  user: process.env.MYSQL_USER,
  password: process.env.MYSQL_PASSWORD,
  createDatabaseTable: true
});

app.set("view engine", "ejs");
app.set("views", __dirname + "/../views");

app.use(express.urlencoded({ extended: true }));
app.use(express.json());
app.use(express.static(__dirname + "/../public"));

app.use(
  session({
    name: "todo.sid",
    secret: process.env.SESSION_SECRET,
    resave: false,
    saveUninitialized: false,
    store: sessionStore,
    cookie: {
      httpOnly: true,
      sameSite: "lax",
      secure: false,
      maxAge: 1000 * 60 * 60
    }
  })
);

app.use(passport.initialize());
app.use(passport.session());

app.get("/", (req, res) => {
  if (req.isAuthenticated && req.isAuthenticated()) {
    return res.redirect("/todos");
  }

  res.render("login", {
    error: req.query.error || null
  });
});

app.use("/auth", authRoutes);
app.use("/todos", todoRoutes);

app.use((req, res) => {
  res.status(404).render("error", {
    status: 404,
    message: "Page not found"
  });
});

app.use((err, req, res, next) => {
  console.error(err);

  res.status(500).render("error", {
    status: 500,
    message: "Internal server error"
  });
});

async function start() {
  await initDb();

  const port = Number(process.env.PORT || 3000);

  app.listen(port, () => {
    console.log(`Todo application running at http://localhost:${port}`);
    console.log("Keycloak: http://localhost:8080");
  });
}

start().catch((err) => {
  console.error("Application startup failed:", err);
  process.exit(1);
});
