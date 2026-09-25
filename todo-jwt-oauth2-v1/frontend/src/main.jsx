import React, {useEffect, useState} from 'react';
import {createRoot} from 'react-dom/client';
import axios from 'axios';
import './style.css';

const API='http://localhost:8081';

function App(){
  const [token,setToken]=useState(localStorage.getItem('token'));
  const [username,setUsername]=useState('');
  const [password,setPassword]=useState('');
  const [title,setTitle]=useState('');
  const [todos,setTodos]=useState([]);

  useEffect(()=>{
    const p=new URLSearchParams(location.search);
    const oauthToken=p.get('token');
    if(oauthToken){localStorage.setItem('token',oauthToken);setToken(oauthToken);history.replaceState({},'', '/');}
  },[]);

  const load=async()=>{
    if(!token)return;
    const r=await axios.get(API+'/api/todos',{headers:{Authorization:`Bearer ${token}`}});
    setTodos(r.data);
  };
  useEffect(()=>{load()},[token]);

  async function login(){
    const r=await axios.post(API+'/api/auth/login',{username,password});
    localStorage.setItem('token',r.data.accessToken);setToken(r.data.accessToken);
  }
  async function add(){
    await axios.post(API+'/api/todos',{title,completed:false},{headers:{Authorization:`Bearer ${token}`}});
    setTitle('');load();
  }
  async function remove(id){
    await axios.delete(API+'/api/todos/'+id,{headers:{Authorization:`Bearer ${token}`}});
    load();
  }
  if(!token) return <main>
    <h1>Todo App</h1>
    <input placeholder="username" value={username} onChange={e=>setUsername(e.target.value)}/>
    <input placeholder="password" type="password" value={password} onChange={e=>setPassword(e.target.value)}/>
    <button onClick={login}>Login with JWT</button>
    <button onClick={()=>location.href=API+'/oauth2/authorization/keycloak'}>Login with Keycloak OAuth2</button>
    <p>For local JWT login, register using POST /api/auth/register.</p>
  </main>;

  return <main>
    <h1>Todo App</h1>
    <button onClick={()=>{localStorage.removeItem('token');setToken(null)}}>Logout</button>
    <div><input value={title} onChange={e=>setTitle(e.target.value)} placeholder="Todo title"/><button onClick={add}>Add</button></div>
    <ul>{todos.map(t=><li key={t.id}>{t.title} <button onClick={()=>remove(t.id)}>Delete</button></li>)}</ul>
  </main>
}
createRoot(document.getElementById('root')).render(<App/>);
