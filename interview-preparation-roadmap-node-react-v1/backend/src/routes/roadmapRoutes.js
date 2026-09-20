import {Router} from 'express';import {getRoadmap} from '../controllers/roadmapController.js';
const r=Router();r.get('/',getRoadmap);export default r;
