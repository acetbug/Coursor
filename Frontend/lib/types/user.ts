export interface User {
  id: string;
  password: string;
  name: string;
  userRole: UserRole;
}

export enum UserRole {
  Student = "Student",
  Teacher = "Teacher",
  Admin = "Admin",
}
