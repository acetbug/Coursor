"use client";

import { useAppBar } from "@/context/appBarContext";
import { useAuth } from "@/context/authContext";
import { Role } from "@/types/userService";
import { ReactNode, useEffect } from "react";
import { useNotification } from "@/context/notificationContext";
import adminTheme from "@/themes/adminTheme";

export default function AdminLayout({ children }: { children: ReactNode }) {
  const { checkAuthStatus, jumpDefault } = useAuth();
  const { setAppBar } = useAppBar();
  const { notify } = useNotification();

  useEffect(() => {
    checkAuthStatus(Role.Admin).then((result) => {
      if (result instanceof Error) {
        notify(result.message);
        jumpDefault();
      }
    });
    setAppBar({
      theme: adminTheme,
      tabs: [
        { label: "用户", href: "/admin/users" },
        { label: "院系", href: "/admin/departments" },
        { label: "学期", href: "/admin/terms" },
        { label: "学籍", href: "/admin/students" },
        { label: "科目", href: "/admin/subjects" },
        { label: "培养", href: "/admin/curricula" },
      ],
    });
  }, [checkAuthStatus, jumpDefault, notify, setAppBar]);

  return children;
}
