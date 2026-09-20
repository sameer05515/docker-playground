import {getProgress,saveProgress} from '../services/progressService.js';
export async function get(req,res,next){try{res.json(await getProgress())}catch(e){next(e)}}
export async function save(req,res,next){try{res.json(await saveProgress(req.body))}catch(e){next(e)}}
