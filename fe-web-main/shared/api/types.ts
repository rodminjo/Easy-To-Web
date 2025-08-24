// API Response Types
import {Permission} from "../../components/types/common/status";

export interface ApiResponse<T> {
  success: boolean;
  data: T | null;
  errors: ApiError | null;
}

// Account Types
export interface Account {
  id: string;
  email: string;
  nickname?: string;
  password: string;
  profileUrl?: string;
}

export interface AccountJoinRequest {
  email: string;
  password: string;
  nickname?: string;
  certificationCode: string;
  type: "VERIFIED_EMAIL";
}

export interface AccountLoginRequest {
  email: string;
  password: string;
}

export interface AccountUpdateRequest {
  nickname: string;
  profileUrl: string;
}

export interface AccountPasswordChangeRequest {
  email: string;
  password: string;
  certificationCode: string;
  type: "PASSWORD_CHANGE_EMAIL";
}

// Mail Types
export interface MailSendRequest {
  email: string;
}

export interface MailCertificationRequest {
  email: string;
  certificationCode: string;
  type: "VERIFIED_EMAIL" | "PASSWORD_CHANGE_EMAIL";
}

// Token Types
export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  account: AccountResponse;
}

export interface AccountResponse {
  id: string;
  nickname: string;
  email: string;
  profileUrl:string;
}


// Error Types
export interface ApiError {
  errorCode: string;
  errorDescription: string;
}

export interface PromiseError {
  success: boolean,
  error: string
}

// Project Types
export interface Project {
  id: string;
  thumbnailUrl: string;
  title: string;
  description: string;
  status: "CLOSED" | "OPEN";
  createDate: string;
  joinedDate?:string;
  members: ProjectMember[];
  memberCount: number;
  publishUrl: string | null;
}

export interface ProjectMember {
  accountId: string;
  email: string;
  nickname: string;
  profileUrl: string;
  permission: Permission;
}


export interface ProjectCreateRequest {
  title: string;
  description: string;
}

export interface ProjectUpdateRequest {
  id: string;
  title: string;
  description: string;
}

export interface ProjectInviteRequest {
  projectId: string;
  email: string;
}

export interface ProjectInviteAcceptRequest {
  code: string;
}

export interface ProjectMemberPermissionUpdateRequest {
  projectId: string;
  accountId: string;
  permission: Permission;
}

export interface ProjectMemberKickRequest {
  projectId: string;
  accountId: string;
}

export interface ProjectHistory {
  id: number;
  content: string;
  editTime: string;
  editor: string[];
}

export interface ProjectHistoryList {
  totalCount: number;
  projectHistories: ProjectHistory[];
}

export interface ProjectPublishResponse {
  url: string;
}

export interface ProjectPublishContent {
  content: string;
}

export interface ProjectUpdateThumbnailRequest {
  id: string;
  thumbnailFileId: string;
}
