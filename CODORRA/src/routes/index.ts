import { Router } from 'express';
import { assessmentRouter } from './assessment.routes';
import { auditRouter } from './audit.routes';
import { adminRouter } from './admin.routes';
import { civicVaultRouter } from './civicvault.routes';
import { emergencyRouter } from './emergency.routes';
import { evidenceRouter } from './evidence.routes';

export const apiRouter = Router();

apiRouter.use('/assessments', assessmentRouter);
apiRouter.use('/audit', auditRouter);
apiRouter.use('/admin', adminRouter);
apiRouter.use('/civicvault', civicVaultRouter);
apiRouter.use('/emergency', emergencyRouter);
apiRouter.use('/evidence', evidenceRouter);
