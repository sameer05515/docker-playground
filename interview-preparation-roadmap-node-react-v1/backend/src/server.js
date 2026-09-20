import 'dotenv/config';import express from 'express';import cors from 'cors';import morgan from 'morgan';
import {connectDb} from './config/db.js';import roadmapRoutes from './routes/roadmapRoutes.js';import progressRoutes from './routes/progressRoutes.js';import {errorHandler} from './middleware/errorHandler.js';
const app=express();app.use(cors({origin:process.env.CORS_ORIGIN?.split(',')||true}));app.use(express.json({limit:'2mb'}));app.use(morgan('dev'));
app.get('/api/health',(req,res)=>res.json({status:'UP'}));app.use('/api/roadmap',roadmapRoutes);app.use('/api/progress',progressRoutes);app.use(errorHandler);
const port=Number(process.env.PORT||8080);connectDb().then(()=>app.listen(port,()=>console.log(`API listening on http://localhost:${port}`))).catch(e=>{console.error(e);process.exit(1)});
