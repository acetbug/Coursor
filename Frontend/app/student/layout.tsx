"use client";

import { useAppBar } from "@/context/appBarContext";
import { useAuth } from "@/context/authContext";
import { Role } from "@/types/userService";
import studentTheme from "@/themes/studentTheme";
import { ReactNode, useEffect } from "react";
import { useNotification } from "@/context/notificationContext";

export default function StudentLayout({ children }: { children: ReactNode }) {
  const { checkAuthStatus, jumpDefault } = useAuth();
  const { setAppBar } = useAppBar();
  const { notify } = useNotification();

  useEffect(() => {
    checkAuthStatus(Role.Student).then((result) => {
      if (result instanceof Error) {
        notify(result.message);
        jumpDefault();
      }
    });
    setAppBar({
      theme: studentTheme,
      tabs: [{ label: "选课", href: "/student/enroll" }],
    });
  }, [checkAuthStatus, jumpDefault, notify, setAppBar]);

  return children;
}
