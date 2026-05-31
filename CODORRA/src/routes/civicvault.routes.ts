import { Router } from 'express';
import { z } from 'zod';
import { authenticate } from '../middleware/auth';
import { asyncHandler, AppError } from '../utils/http';
import {
  decideDocumentRequest,
  emergencyRevokeAll,
  getDashboard,
  toggleDocumentAccess
} from '../services/civicvault.service';

const sectionSchema = z.enum(['bank', 'govt']);
const decisionSchema = z.object({
  decision: z.enum(['approved', 'denied'])
});

export const civicVaultRouter = Router();

civicVaultRouter.get('/dashboard', authenticate, asyncHandler(async (req, res) => {
  const dashboard = await getDashboard(req.profile!);
  res.json({ dashboard });
}));

civicVaultRouter.patch('/documents/:section/:documentId/access', authenticate, asyncHandler(async (req, res) => {
  const section = sectionSchema.parse(req.params.section);
  const documentId = String(req.params.documentId);

  try {
    const dashboard = await toggleDocumentAccess(req.profile!, section, documentId);
    res.json({ dashboard });
  } catch (error) {
    throw new AppError(404, error instanceof Error ? error.message : 'Document not found');
  }
}));

civicVaultRouter.post('/documents/:section/:documentId/requests/:requestId/decision', authenticate, asyncHandler(async (req, res) => {
  const section = sectionSchema.parse(req.params.section);
  const documentId = String(req.params.documentId);
  const requestId = String(req.params.requestId);
  const payload = decisionSchema.parse(req.body);

  try {
    const dashboard = await decideDocumentRequest(req.profile!, section, documentId, requestId, payload.decision);
    res.json({ dashboard });
  } catch (error) {
    throw new AppError(404, error instanceof Error ? error.message : 'Request not found');
  }
}));

civicVaultRouter.post('/emergency/stop', authenticate, asyncHandler(async (req, res) => {
  const dashboard = await emergencyRevokeAll(req.profile!);
  res.json({ dashboard });
}));