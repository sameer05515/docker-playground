import mongoose from 'mongoose';
const PhaseSchema=new mongoose.Schema({
 phaseId:{type:String,unique:true},week:String,title:{type:String,required:true},description:String,
 topics:[{topicId:String,title:String,summary:String,difficulty:String,minutes:Number,tags:[String],questions:[String]}]
},{timestamps:true});
export default mongoose.model('Phase',PhaseSchema);
