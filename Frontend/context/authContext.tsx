// app/context/AuthContext.tsx
"use client";

import React, {
  createContext,
  useState,
  useContext,
  ReactNode,
  useCallback,
  useEffect,
} from "react";
import { useRouter } from "next/navigation";
import { Role } from "@/types/userService";
import UserService from "@/services/userService";
import Result from "@/types/result";

interface UserInfo {
  id: string;
  name: string;
  token: string;
  role: Role;
}

interface AuthContextType {
  userInfo: UserInfo | undefined | null;
  login: (userInfo: UserInfo) => void;
  logout: () => void;
  checkAuthStatus: (role: Role) => Promise<Result<void>>;
  jumpDefault: (givenUserInfo?: UserInfo) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [userInfo, setUserInfo] = useState<UserInfo | undefined | null>(
    undefined
  );
  const router = useRouter();

  const login = useCallback((userInfo: UserInfo) => {
    setUserInfo(userInfo);
    localStorage.setItem("user", JSON.stringify(userInfo));
  }, []);

  const logout = useCallback(() => {
    if (userInfo) UserService.logout({ token: userInfo.token });
    setUserInfo(null);
    localStorage.removeItem("user");
  }, [userInfo]);

  const checkAuthStatus = useCallback(
    async (role: Role) => {
      if (userInfo === undefined) return;
      if (userInfo === null) return new Error("User not logged in");
      if (userInfo.role !== role) {
        logout();
        return new Error("Role mismatch");
      }
      const result = await UserService.checkAuth({
        token: userInfo.token,
        role: userInfo.role,
      });
      if (result instanceof Error) {
        logout();
        return result;
      }
      if (userInfo.id !== result) {
        logout();
        return new Error("User ID mismatch");
      }
    },
    [logout, userInfo]
  );

  const jumpDefault = useCallback(
    (givenUserInfo?: UserInfo) => {
      const usedUserInfo = givenUserInfo || userInfo;
      router.push(
        "/" + (usedUserInfo?.role ? usedUserInfo.role.toLowerCase() : "login")
      );
    },
    [router, userInfo]
  );

  useEffect(() => {
    const storedUserInfo = localStorage.getItem("user");
    setUserInfo(storedUserInfo ? JSON.parse(storedUserInfo) : null);
  }, []);

  return (
    <AuthContext.Provider
      value={{ userInfo, login, logout, checkAuthStatus, jumpDefault }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
