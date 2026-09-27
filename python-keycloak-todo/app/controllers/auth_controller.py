from flask import Blueprint, redirect, session, url_for
from app import oauth

auth_bp = Blueprint('auth', __name__)

@auth_bp.get('/login')
def login():
    redirect_uri = url_for('auth.authorize', _external=True)
    return oauth.keycloak.authorize_redirect(redirect_uri)

@auth_bp.get('/authorize')
def authorize():
    token = oauth.keycloak.authorize_access_token()
    userinfo = token.get('userinfo') or oauth.keycloak.userinfo()
    session['user'] = {
        'sub': userinfo.get('sub'),
        'username': userinfo.get('preferred_username') or userinfo.get('email'),
        'email': userinfo.get('email'),
        'name': userinfo.get('name') or userinfo.get('preferred_username'),
    }
    session['token'] = token
    return redirect(url_for('todo.list_todos'))

@auth_bp.get('/logout')
def logout():
    session.clear()
    return redirect(url_for('todo.home'))
