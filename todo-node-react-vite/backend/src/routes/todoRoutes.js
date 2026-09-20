import { Router } from "express";
import {
  findAll,
  findById,
  create,
  update,
  remove,
  toggle
} from "../data/todoStore.js";

const router = Router();

router.get("/", (req, res) => {
  res.json(findAll());
});

router.get("/:id", (req, res) => {
  const todo = findById(Number(req.params.id));

  if (!todo) {
    return res.status(404).json({ message: "Todo not found" });
  }

  res.json(todo);
});

router.post("/", (req, res) => {
  const { title, description } = req.body;

  if (!title || !title.trim()) {
    return res.status(400).json({ message: "Title is required" });
  }

  const todo = create({
    title: title.trim(),
    description: description?.trim() ?? ""
  });

  res.status(201).json(todo);
});

router.put("/:id", (req, res) => {
  const id = Number(req.params.id);

  if (req.body.title !== undefined && !req.body.title.trim()) {
    return res.status(400).json({ message: "Title cannot be empty" });
  }

  const todo = update(id, req.body);

  if (!todo) {
    return res.status(404).json({ message: "Todo not found" });
  }

  res.json(todo);
});

router.patch("/:id/toggle", (req, res) => {
  const todo = toggle(Number(req.params.id));

  if (!todo) {
    return res.status(404).json({ message: "Todo not found" });
  }

  res.json(todo);
});

router.delete("/:id", (req, res) => {
  const todo = remove(Number(req.params.id));

  if (!todo) {
    return res.status(404).json({ message: "Todo not found" });
  }

  res.status(204).send();
});

export default router;
