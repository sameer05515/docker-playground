import Phase from '../models/Phase.js';
export async function getRoadmap(req,res,next){try{res.json(await Phase.find().sort({phaseId:1}).lean())}catch(e){next(e)}}
