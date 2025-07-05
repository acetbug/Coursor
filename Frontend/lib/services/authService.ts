import { Auth } from "../types/auth";
import { UserRole } from "../types/user";
import ApiClient from "./apiClient";

const port = 10010;
const client = new ApiClient(port);

const AuthService = {
  checkAuth: client.api<{ token: string; userRole: UserRole }, unknown>(
    "CheckAuthMessage"
  ),

  createAuth: client.api<{ userId: string; password: string }, Auth>(
    "CreateAuthMessage"
  ),

  deleteAuth: client.api<{ token: string }, unknown>("DeleteAuthMessage"),
};

export default AuthService;
