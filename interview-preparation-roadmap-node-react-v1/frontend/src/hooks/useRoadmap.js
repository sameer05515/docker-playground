import {useCallback,useEffect,useState} from 'react';import {roadmapApi,progressApi} from '../services/api';
export function useRoadmap(){const[phases,setPhases]=useState([]),[progress,setProgress]=useState({status:{},notes:{},routine:{},activity:{},streak:0,mockScore:0}),[loading,setLoading]=useState(true);
const refresh=useCallback(async()=>{setLoading(true);try{const[r,p]=await Promise.all([roadmapApi.get(),progressApi.get()]);setPhases(r.data);setProgress(p.data)}finally{setLoading(false)}},[]);
useEffect(()=>{refresh()},[refresh]);const save=async d=>{setProgress(d);await progressApi.save(d)};return{phases,progress,loading,save}};
