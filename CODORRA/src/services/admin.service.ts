import { AuthUser } from '../domain';
import { store } from '../store';

export async function createAccessRequest(actor: AuthUser, input: { targetUserId: string; officerName: string; reason: string; caseId: string }) {
  const request = await store.createGovernmentRequest({
    target_user_id: input.targetUserId,
    officer_name: input.officerName,
    reason: input.reason,
    case_id: input.caseId,
    status: 'pending',
    decided_by: null,
    decided_at: null
  });

  await store.addAuditLog({
    actor_id: actor.id,
    actor_role: actor.role,
    action: 'Access Requested',
    entity_type: 'government_requests',
    entity_id: request.id,
    metadata: {
      targetUserId: input.targetUserId,
      officerName: input.officerName,
      reason: input.reason,
      caseId: input.caseId
    }
  });

  return request;
}

export async function decideAccessRequest(actor: AuthUser, requestId: string, decision: 'approved' | 'rejected') {
  const request = await store.updateGovernmentRequest(requestId, {
    status: decision,
    decided_by: actor.id,
    decided_at: new Date().toISOString()
  });

  if (request) {
    await store.addAuditLog({
      actor_id: actor.id,
      actor_role: actor.role,
      action: decision === 'approved' ? 'Access Granted' : 'Access Denied',
      entity_type: 'government_requests',
      entity_id: request.id,
      metadata: {
        caseId: request.case_id,
        targetUserId: request.target_user_id,
        officerName: request.officer_name
      }
    });
  }

  return request;
}

export async function listAccessRequests(targetUserId?: string) {
  return store.listGovernmentRequests(targetUserId);
}
