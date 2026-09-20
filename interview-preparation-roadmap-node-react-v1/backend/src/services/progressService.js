import UserProgress from '../models/UserProgress.js';
export async function getProgress(){let d=await UserProgress.findOne({userKey:'local-user'});return d||UserProgress.create({userKey:'local-user'});}
export async function saveProgress(body){
 const keys=['status','notes','routine','activity','streak','mockScore']; const update={};
 for(const k of keys) if(body[k]!==undefined) update[k]=body[k];
 return UserProgress.findOneAndUpdate({userKey:'local-user'},{$set:update},{upsert:true,new:true,setDefaultsOnInsert:true});
}
