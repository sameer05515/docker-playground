import mongoose from 'mongoose';
export async function connectDb(){
 const uri=process.env.MONGODB_URI||'mongodb://localhost:27017/interview_roadmap';
 await mongoose.connect(uri); console.log('MongoDB connected');
}
