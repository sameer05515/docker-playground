export default function TodoItem({
  todo,
  onToggle,
  onDelete,
  onUpdate
}) {
  const handleEdit = async () => {
    const title = window.prompt("Enter todo title:", todo.title);

    if (title === null || !title.trim()) return;

    await onUpdate(todo.id, {
      title: title.trim(),
      description: todo.description,
      completed: todo.completed
    });
  };

  return (
    <li className={`todo-item ${todo.completed ? "completed" : ""}`}>
      <div className="todo-content">
        <input
          type="checkbox"
          checked={todo.completed}
          onChange={() => onToggle(todo.id)}
        />

        <div>
          <h3>{todo.title}</h3>
          {todo.description && <p>{todo.description}</p>}
        </div>
      </div>

      <div className="todo-actions">
        <button onClick={handleEdit}>Edit</button>
        <button className="danger" onClick={() => onDelete(todo.id)}>
          Delete
        </button>
      </div>
    </li>
  );
}
