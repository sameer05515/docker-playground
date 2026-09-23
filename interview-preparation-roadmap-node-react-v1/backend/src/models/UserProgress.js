import mongoose from 'mongoose';
const schema=new mongoose.Schema({
 userKey:{type:String,unique:true,default:'local-user'},
 status:{type:Map,of:String,default:{}},notes:{type:Map,of:String,default:{}},
 routine:{type:Map,of:Boolean,default:{}},activity:{type:Map,of:Number,default:{}},
 streak:{type:Number,default:0},mockScore:{type:Number,default:0}
},{timestamps:true});
export default mongoose.model('UserProgress',schema);
