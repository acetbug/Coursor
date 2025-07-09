"use client";

import { Stack, Tab, Tabs } from "@mui/material";
import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";

export default function AdminUsersLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const router = useRouter();
  const [tabValue, setTabValue] = useState("admin");

  useEffect(() => {
    router.push(`/admin/users/${tabValue}`);
  }, [router, tabValue]);

  return (
    <Stack display="flex" flex={1} p={2} spacing={2}>
      <Tabs value={tabValue} onChange={(_, newValue) => setTabValue(newValue)}>
        <Tab label="管理员" value="admin" />
        <Tab label="教师" value="teacher" />
        <Tab label="学生" value="student" />
      </Tabs>
      {children}
    </Stack>
  );
}
