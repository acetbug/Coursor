export interface UserAuth {
  token: string;
  name: string;
  role: Role;
}

export interface User {
  id: string;
  name: string;
}

export enum Role {
  Admin = "Admin",
  Student = "Student",
  Teacher = "Teacher",
}
