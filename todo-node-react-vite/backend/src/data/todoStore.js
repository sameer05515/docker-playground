let nextId = 4;

let todos = [
  {
    id: 1,
    title: "Learn Node.js",
    description: "Build a REST API with Express",
    completed: false,
    createdAt: new Date().toISOString()
  },
  {
    id: 2,
    title: "Learn React",
    description: "Build UI with React and Vite",
    completed: false,
    createdAt: new Date().toISOString()
  },
  {
    id: 3,
    title: "Practice Docker",
    description: "Containerize the application",
    completed: true,
    createdAt: new Date().toISOString()
  }
];

export function findAll() {
  return [...todos];
}

export function findById(id) {
  return todos.find(todo => todo.id === id);
}

export function create(data) {
  const todo = {
    id: nextId++,
    title: data.title,
    description: data.description ?? "",
    completed: false,
    createdAt: new Date().toISOString()
  };

  todos.push(todo);
  return todo;
}

export function update(id, data) {
  const todo = findById(id);

  if (!todo) {
    return null;
  }

  if (data.title !== undefined) todo.title = data.title;
  if (data.description !== undefined) todo.description = data.description;
  if (data.completed !== undefined) todo.completed = Boolean(data.completed);

  return todo;
}

export function remove(id) {
  const index = todos.findIndex(todo => todo.id === id);

  if (index === -1) {
    return null;
  }

  return todos.splice(index, 1)[0];
}

export function toggle(id) {
  const todo = findById(id);

  if (!todo) {
    return null;
  }

  todo.completed = !todo.completed;
  return todo;
}
