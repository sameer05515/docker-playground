const express = require("express");
const { pool } = require("../db");
const { requireAuth } = require("../middleware/auth");

const router = express.Router();

router.use(requireAuth);

router.get("/", async (req, res, next) => {
  try {
    const [todos] = await pool.query(
      "SELECT * FROM todos WHERE user_id = ? ORDER BY id DESC",
      [req.user.id]
    );

    res.render("todos", {
      user: req.user,
      todos,
      error: req.query.error || null
    });
  } catch (err) {
    next(err);
  }
});

router.post("/", async (req, res, next) => {
  try {
    const title = String(req.body.title || "").trim();
    const description = String(req.body.description || "").trim();

    if (!title) {
      return res.redirect("/todos?error=Title%20is%20required");
    }

    await pool.query(
      "INSERT INTO todos (user_id, title, description) VALUES (?, ?, ?)",
      [req.user.id, title, description]
    );

    res.redirect("/todos");
  } catch (err) {
    next(err);
  }
});

router.post("/:id/toggle", async (req, res, next) => {
  try {
    await pool.query(
      "UPDATE todos SET completed = NOT completed WHERE id = ? AND user_id = ?",
      [req.params.id, req.user.id]
    );

    res.redirect("/todos");
  } catch (err) {
    next(err);
  }
});

router.post("/:id/delete", async (req, res, next) => {
  try {
    await pool.query(
      "DELETE FROM todos WHERE id = ? AND user_id = ?",
      [req.params.id, req.user.id]
    );

    res.redirect("/todos");
  } catch (err) {
    next(err);
  }
});

module.exports = router;
