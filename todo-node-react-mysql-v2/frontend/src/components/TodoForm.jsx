import { useState } from "react";

export default function TodoForm({ onAdd }) {
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");

  const handleSubmit = async event => {
    event.preventDefault();

    if (!title.trim()) return;

    await onAdd({
      title: title.trim(),
      description: description.trim()
    });

    setTitle("");
    setDescription("");
  };

  return (
    <form className="todo-form" onSubmit={handleSubmit}>
      <input
        value={title}
        onChange={event => setTitle(event.target.value)}
        placeholder="Todo title"
      />

      <input
        value={description}
        onChange={event => setDescription(event.target.value)}
        placeholder="Description"
      />

      <button type="submit">Add Todo</button>
    </form>
  );
}
