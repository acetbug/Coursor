import { UserRole } from "./user";

export interface Auth {
  token: string;
  userRole: UserRole;
  expiresAt: number;
}
