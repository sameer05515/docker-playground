import axios from 'axios';
const api=axios.create({baseURL:import.meta.env.VITE_API_BASE_URL||'http://localhost:8080/api'});
export const roadmapApi={get:()=>api.get('/roadmap')};
export const progressApi={get:()=>api.get('/progress'),save:data=>api.put('/progress',data)};
