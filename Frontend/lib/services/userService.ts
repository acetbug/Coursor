import { User, UserRole } from "../types/user";
import ApiClient from "./apiClient";

const port = 10018;
const client = new ApiClient(port);

const UserService = {
  checkUser: client.api<{ userId: string; password: string }, UserRole>(
    "CheckUserMessage"
  ),

  deleteUser: client.api<{ adminToken: string; userId: string }, unknown>(
    "DeleteUserMessage"
  ),

  queryUsers: client.api<{ userRole: UserRole }, User[]>("QueryUsersMessage"),

  updateUser: client.api<{ adminToken: string; user: User }, unknown>(
    "UpdateUserMessage"
  ),
};

export default UserService;
