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
  userInfo: UserInfo | null;
  login: (userInfo: UserInfo) => void;
  logout: () => void;
  checkAuthStatus: () => Promise<Result<Role>>;
  jumpDefault: (givenUserInfo?: UserInfo) => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [userInfo, setUserInfo] = useState<UserInfo | null>(null);
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

  const checkAuthStatus = useCallback(async () => {
    if (!userInfo) return new Error("User not logged in");
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
    return userInfo.role;
  }, [logout, userInfo]);

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
    if (storedUserInfo) setUserInfo(JSON.parse(storedUserInfo));
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
