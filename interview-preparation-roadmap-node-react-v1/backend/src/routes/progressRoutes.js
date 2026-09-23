import {Router} from 'express';import {get,save} from '../controllers/progressController.js';
const r=Router();r.get('/',get);r.put('/',save);export default r;
