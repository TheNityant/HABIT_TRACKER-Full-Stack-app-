import {
  AuthUser,
  AuditLogEntry,
  EmergencyProfile,
  EvidenceRecord,
  GovernmentRequest,
  NewAuditLogEntry,
  NewEmergencyProfile,
  NewEvidenceRecord,
  NewGovernmentRequest,
  UpdateGovernmentRequest,
  UserProfile
} from '../domain';

export interface DataStore {
  kind: 'memory' | 'supabase';
  ensureUserProfile(user: AuthUser): Promise<UserProfile>;
  getUserProfile(userId: string): Promise<UserProfile | null>;
  upsertEmergencyProfile(profile: NewEmergencyProfile): Promise<EmergencyProfile>;
  getEmergencyProfile(userId: string): Promise<EmergencyProfile | null>;
  addEvidence(record: NewEvidenceRecord): Promise<EvidenceRecord>;
  listEvidence(userId: string): Promise<EvidenceRecord[]>;
  createGovernmentRequest(request: NewGovernmentRequest): Promise<GovernmentRequest>;
  updateGovernmentRequest(requestId: string, patch: UpdateGovernmentRequest): Promise<GovernmentRequest | null>;
  listGovernmentRequests(targetUserId?: string): Promise<GovernmentRequest[]>;
  addAuditLog(entry: NewAuditLogEntry): Promise<AuditLogEntry>;
  listAuditLogs(actorId?: string): Promise<AuditLogEntry[]>;
}
