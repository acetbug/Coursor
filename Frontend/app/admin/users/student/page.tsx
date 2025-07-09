"use client";

import UserTable from "@/components/userTable";
import { useNotification } from "@/context/notificationContext";
import UserService from "@/services/userService";
import { Role, User } from "@/types/userService";
import { useEffect, useState } from "react";

export default function AdminUsersStudentPage() {
  const { notify } = useNotification();
  const [flag, setFlag] = useState(false);
  const [userList, setUserList] = useState<User[]>([]);

  useEffect(() => {
    UserService.queryUsers({ role: Role.Student }).then((result) => {
      if (result instanceof Error) {
        notify(result.message);
        return;
      } else setUserList(result);
    });
  }, [flag, notify]);

  return (
    <UserTable userList={userList} onChange={() => setFlag((prev) => !prev)} />
  );
}
