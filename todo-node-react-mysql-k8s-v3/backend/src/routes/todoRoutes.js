import { Router } from "express";
import { getPool } from "../db/mysql.js";

const router = Router();

function mapTodo(row) {
  return {
    id: row.id,
    title: row.title,
    description: row.description,
    completed: Boolean(row.completed),
    createdAt: row.created_at,
    updatedAt: row.updated_at
  };
}

router.get("/", async (req, res, next) => {
  try {
    const [rows] = await getPool().execute(
      `SELECT id, title, description, completed, created_at, updated_at
       FROM todos
       ORDER BY id DESC`
    );

    res.json(rows.map(mapTodo));
  } catch (error) {
    next(error);
  }
});

router.get("/:id", async (req, res, next) => {
  try {
    const [rows] = await getPool().execute(
      `SELECT id, title, description, completed, created_at, updated_at
       FROM todos
       WHERE id = ?`,
      [Number(req.params.id)]
    );

    if (rows.length === 0) {
      return res.status(404).json({ message: "Todo not found" });
    }

    res.json(mapTodo(rows[0]));
  } catch (error) {
    next(error);
  }
});

router.post("/", async (req, res, next) => {
  try {
    const title = req.body.title?.trim();
    const description = req.body.description?.trim() || "";

    if (!title) {
      return res.status(400).json({
        message: "Title is required"
      });
    }

    const [result] = await getPool().execute(
      `INSERT INTO todos (title, description)
       VALUES (?, ?)`,
      [title, description]
    );

    const [rows] = await getPool().execute(
      `SELECT id, title, description, completed, created_at, updated_at
       FROM todos
       WHERE id = ?`,
      [result.insertId]
    );

    res.status(201).json(mapTodo(rows[0]));
  } catch (error) {
    next(error);
  }
});

router.put("/:id", async (req, res, next) => {
  try {
    const id = Number(req.params.id);
    const title = req.body.title?.trim();
    const description = req.body.description?.trim() || "";
    const completed = Boolean(req.body.completed);

    if (!title) {
      return res.status(400).json({
        message: "Title is required"
      });
    }

    const [result] = await getPool().execute(
      `UPDATE todos
       SET title = ?, description = ?, completed = ?
       WHERE id = ?`,
      [title, description, completed, id]
    );

    if (result.affectedRows === 0) {
      return res.status(404).json({ message: "Todo not found" });
    }

    const [rows] = await getPool().execute(
      `SELECT id, title, description, completed, created_at, updated_at
       FROM todos
       WHERE id = ?`,
      [id]
    );

    res.json(mapTodo(rows[0]));
  } catch (error) {
    next(error);
  }
});

router.patch("/:id/toggle", async (req, res, next) => {
  try {
    const id = Number(req.params.id);

    const [result] = await getPool().execute(
      `UPDATE todos
       SET completed = NOT completed
       WHERE id = ?`,
      [id]
    );

    if (result.affectedRows === 0) {
      return res.status(404).json({ message: "Todo not found" });
    }

    const [rows] = await getPool().execute(
      `SELECT id, title, description, completed, created_at, updated_at
       FROM todos
       WHERE id = ?`,
      [id]
    );

    res.json(mapTodo(rows[0]));
  } catch (error) {
    next(error);
  }
});

router.delete("/:id", async (req, res, next) => {
  try {
    const [result] = await getPool().execute(
      "DELETE FROM todos WHERE id = ?",
      [Number(req.params.id)]
    );

    if (result.affectedRows === 0) {
      return res.status(404).json({ message: "Todo not found" });
    }

    res.status(204).send();
  } catch (error) {
    next(error);
  }
});

export default router;
