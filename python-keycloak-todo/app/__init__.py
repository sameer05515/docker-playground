import os
from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from authlib.integrations.flask_client import OAuth
from dotenv import load_dotenv

load_dotenv()

db = SQLAlchemy()
oauth = OAuth()

def create_app():
    app = Flask(__name__)
    app.config.from_mapping(
        SECRET_KEY=os.getenv('FLASK_SECRET_KEY', 'change-me-in-production'),
        SQLALCHEMY_DATABASE_URI=os.getenv(
            'DATABASE_URL',
            'mysql+pymysql://todo_user:todo_password@localhost:3306/todo_db'
        ),
        SQLALCHEMY_TRACK_MODIFICATIONS=False,
        SESSION_COOKIE_HTTPONLY=True,
        SESSION_COOKIE_SAMESITE='Lax',
    )

    db.init_app(app)
    oauth.init_app(app)

    oauth.register(
        name='keycloak',
        client_id=os.getenv('KEYCLOAK_CLIENT_ID', 'todo-app'),
        client_secret=os.getenv('KEYCLOAK_CLIENT_SECRET', 'todo-app-secret'),
        server_metadata_url=os.getenv(
            'KEYCLOAK_SERVER_METADATA_URL',
            'http://localhost:8080/realms/todo-realm/.well-known/openid-configuration'
        ),
        client_kwargs={'scope': 'openid profile email'},
    )

    from app.controllers.auth_controller import auth_bp
    from app.controllers.todo_controller import todo_bp
    app.register_blueprint(auth_bp)
    app.register_blueprint(todo_bp)

    with app.app_context():
        from app.models.todo import Todo
        db.create_all()

    return app
