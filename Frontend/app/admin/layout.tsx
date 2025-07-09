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
    checkAuthStatus().then((result) => {
      if (result !== Role.Admin) {
        if (result instanceof Error) notify(result.message);
        else notify("Unauthorized access");
        jumpDefault();
      }
    });
    setAppBar({
      theme: adminTheme,
      tabs: [{ label: "用户", href: "/admin/users" }],
    });
  }, [checkAuthStatus, jumpDefault, notify, setAppBar]);

  return children;
}
