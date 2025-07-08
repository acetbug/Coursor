import { Role, User, UserAuth } from "../types/userService";
import ApiClient from "./apiClient";

const port = 10010;
const client = new ApiClient(port);

const UserService = {
  login: client.api<{ userId: string; password: string }, UserAuth>(
    "LoginMessage"
  ),

  logout: client.api<{ token: string }, void>("LogoutMessage"),

  checkAuth: client.api<{ token: string; role: Role }, unknown>(
    "CheckAuthMessage"
  ),

  checkUserRole: client.api<{ userId: string; role: Role }, void>(
    "CheckUserRoleMessage"
  ),

  createUser: client.api<
    {
      adminToken: string;
      userId: string;
      password: string;
      name: string;
      role: Role;
    },
    void
  >("CreateUserMessage"),

  queryUsers: client.api<{ role: Role }, User[]>("QueryUsersMessage"),

  updateUserName: client.api<
    {
      adminToken: string;
      userId: string;
      name: string;
    },
    void
  >("UpdateUserNameMessage"),

  updateUserPassword: client.api<
    {
      adminToken: string;
      userId: string;
      password: string;
    },
    void
  >("UpdateUserPasswordMessage"),

  deleteUser: client.api<
    {
      adminToken: string;
      userId: string;
    },
    void
  >("DeleteUserMessage"),
};

export default UserService;
