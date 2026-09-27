from functools import wraps
from flask import Blueprint, abort, flash, redirect, render_template, request, session, url_for
from app import db
from app.models.todo import Todo

todo_bp = Blueprint('todo', __name__)

def login_required(view):
    @wraps(view)
    def wrapped(*args, **kwargs):
        if 'user' not in session:
            return redirect(url_for('auth.login'))
        return view(*args, **kwargs)
    return wrapped

@todo_bp.get('/')
def home():
    return render_template('home.html')

@todo_bp.get('/todos')
@login_required
def list_todos():
    username = session['user']['username']
    todos = Todo.query.filter_by(username=username).order_by(Todo.id.desc()).all()
    return render_template('todos/list.html', todos=todos)

@todo_bp.post('/todos')
@login_required
def create_todo():
    title = request.form.get('title', '').strip()
    description = request.form.get('description', '').strip()
    if not title:
        flash('Title is required.', 'error')
        return redirect(url_for('todo.list_todos'))
    db.session.add(Todo(title=title, description=description,
                        username=session['user']['username']))
    db.session.commit()
    flash('Todo created.', 'success')
    return redirect(url_for('todo.list_todos'))

@todo_bp.post('/todos/<int:todo_id>/toggle')
@login_required
def toggle_todo(todo_id):
    todo = Todo.query.filter_by(id=todo_id, username=session['user']['username']).first_or_404()
    todo.completed = not todo.completed
    db.session.commit()
    return redirect(url_for('todo.list_todos'))

@todo_bp.post('/todos/<int:todo_id>/delete')
@login_required
def delete_todo(todo_id):
    todo = Todo.query.filter_by(id=todo_id, username=session['user']['username']).first_or_404()
    db.session.delete(todo)
    db.session.commit()
    flash('Todo deleted.', 'success')
    return redirect(url_for('todo.list_todos'))
