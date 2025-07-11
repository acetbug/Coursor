"use client";

import { useAppBar } from "@/context/appBarContext";
import { useAuth } from "@/context/authContext";
import { Role } from "@/types/userService";
import { ReactNode, useEffect } from "react";
import { useNotification } from "@/context/notificationContext";
import teacherTheme from "@/themes/teacherTheme";

export default function TeacherLayout({ children }: { children: ReactNode }) {
  const { checkAuthStatus, jumpDefault } = useAuth();
  const { setAppBar } = useAppBar();
  const { notify } = useNotification();

  useEffect(() => {
    checkAuthStatus(Role.Teacher).then((result) => {
      if (result instanceof Error) {
        notify(result.message);
        jumpDefault();
      }
    });
    setAppBar({
      theme: teacherTheme,
      tabs: [{ label: "开课", href: "/teacher/teaching" }],
    });
  }, [checkAuthStatus, jumpDefault, notify, setAppBar]);

  return children;
}
