import React,{useEffect,useState} from 'react';
import {createRoot} from 'react-dom/client';
import axios from 'axios';
import './style.css';

const API='http://localhost:8081';

function parseJwt(token){
  try { return JSON.parse(atob(token.split('.')[1].replace(/-/g,'+').replace(/_/g,'/'))); }
  catch { return {}; }
}

function Login({onLogin}){
  const [u,setU]=useState(''),[p,setP]=useState(''),[e,setE]=useState('');
  async function login(x){
    x.preventDefault(); setE('');
    try{
      const r=await axios.post(`${API}/api/auth/login`,{username:u,password:p});
      localStorage.setItem('token',r.data.accessToken); onLogin(r.data.accessToken);
    }catch(x){setE(x.response?.data?.message||'Login failed');}
  }
  return <div className="card">
    <h1>Todo Login</h1>
    <form onSubmit={login}>
      <input placeholder="Username" value={u} onChange={x=>setU(x.target.value)}/>
      <input type="password" placeholder="Password" value={p} onChange={x=>setP(x.target.value)}/>
      <button>Login with Local JWT</button>
    </form>
    <button onClick={()=>location.href=`${API}/oauth2/authorization/keycloak`}>Login with Keycloak</button>
    {e&&<p className="error">{e}</p>}
  </div>
}

function TodoApp({token,onLogout}){
  const claims=parseJwt(token);
  const roles=claims.roles||[];
  const isAdmin=roles.includes('ROLE_ADMIN');
  const [todos,setTodos]=useState([]),[title,setTitle]=useState('');
  const api=axios.create({baseURL:API,headers:{Authorization:`Bearer ${token}`}});

  async function load(){
    try { setTodos((await api.get('/api/todos')).data); }
    catch(e){ if(e.response?.status===401) onLogout(false); }
  }
  useEffect(()=>{load();},[]);

  async function add(){
    if(!title.trim()) return;
    await api.post('/api/todos',{title,completed:false});
    setTitle(''); await load();
  }
  async function toggle(t){
    await api.put(`/api/todos/${t.id}`,{title:t.title,completed:!t.completed});
    await load();
  }
  async function remove(id){await api.delete(`/api/todos/${id}`);await load();}

  return <div className="card wide">
    <div className="header">
      <div><h1>Todo App</h1><small>{isAdmin?'ROLE_ADMIN — Read Only':'ROLE_USER — My Todos'}</small></div>
      <button onClick={()=>onLogout(true)}>Logout</button>
    </div>

    {isAdmin ?
      <p className="info">Admin can view all users' todos. Create, update and delete are disabled.</p> :
      <div className="add"><input value={title} placeholder="Todo title" onChange={x=>setTitle(x.target.value)}/><button onClick={add}>Add</button></div>
    }

    <ul>{todos.map(t=><li key={t.id}>
      <div><strong>{t.title}</strong><small>owner: {t.ownerId}</small></div>
      {!isAdmin && <span><button onClick={()=>toggle(t)}>{t.completed?'Undo':'Done'}</button><button onClick={()=>remove(t.id)}>Delete</button></span>}
    </li>)}</ul>
  </div>
}

function App(){
  const [token,setToken]=useState(localStorage.getItem('token'));

  useEffect(()=>{
    const t=new URLSearchParams(location.search).get('token');
    if(t){localStorage.setItem('token',t);setToken(t);history.replaceState({},document.title,'/');}
  },[]);

  async function logout(doKeycloak){
    const t=localStorage.getItem('token');
    localStorage.removeItem('token');
    setToken(null);
    if(!t) return;

    try{
      const r=await axios.post(`${API}/api/auth/logout`,{}, {headers:{Authorization:`Bearer ${t}`}});
      if(doKeycloak && r.data.logoutUrl){window.location.href=r.data.logoutUrl;}
    }catch(e){/* local state is already cleared */}
  }

  return token ? <TodoApp token={token} onLogout={logout}/> : <Login onLogin={setToken}/>;
}

createRoot(document.getElementById('root')).render(<App/>);
