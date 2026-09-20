import React,{useEffect,useState} from 'react'
import {createRoot} from 'react-dom/client'
import './style.css'
const API='/api/todos'
function App(){
 const [todos,setTodos]=useState([]),[title,setTitle]=useState(''),[error,setError]=useState('')
 const load=async()=>{try{const r=await fetch(API);if(!r.ok)throw Error('Failed to load todos');setTodos(await r.json())}catch(e){setError(e.message)}}
 useEffect(()=>{load()},[])
 const add=async e=>{e.preventDefault();if(!title.trim())return;const r=await fetch(API,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({title:title.trim()})});if(!r.ok){setError('Failed to create todo');return}setTitle('');load()}
 const toggle=async t=>{await fetch(API+'/'+t.id,{method:'PUT',headers:{'Content-Type':'application/json'},body:JSON.stringify({title:t.title,completed:!t.completed})});load()}
 const del=async id=>{await fetch(API+'/'+id,{method:'DELETE'});load()}
 return <div className="container"><h1>Todo Application</h1><p className="sub">Docker + PostgreSQL V2</p>
 <form onSubmit={add} className="form"><input value={title} onChange={e=>setTitle(e.target.value)} placeholder="Enter todo..."/><button>Add</button></form>
 {error&&<div className="error">{error}</div>}<div className="list">{todos.map(t=><div className="todo" key={t.id}><label><input type="checkbox" checked={t.completed} onChange={()=>toggle(t)}/><span className={t.completed?'completed':''}>{t.title}</span></label><button className="delete" onClick={()=>del(t.id)}>Delete</button></div>)}{!todos.length&&<p>No todos yet.</p>}</div></div>
}
createRoot(document.getElementById('root')).render(<App/>)
