"use client";

import { useAuth } from "@/context/authContext";
import { useNotification } from "@/context/notificationContext";
import UserService from "@/services/userService";
import { Role, User } from "@/types/userService";
import { Add, Delete, Edit } from "@mui/icons-material";
import CryptoJS from "crypto-js";
import {
  Box,
  Button,
  Card,
  CardActions,
  CardContent,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Grid,
  IconButton,
  Stack,
  Tab,
  Tabs,
  TextField,
  Typography,
} from "@mui/material";
import { useEffect, useState } from "react";

const tabs = [
  { label: "学生", role: Role.Student },
  { label: "教师", role: Role.Teacher },
  { label: "管理员", role: Role.Admin },
];

export default function AdminUsersPage() {
  const { notify } = useNotification();
  const { userInfo } = useAuth();
  const [flag, setFlag] = useState(false);
  const toggleFlag = () => setFlag((prev) => !prev);
  const [selected, setSelected] = useState(0);
  const [userList, setUserList] = useState<User[]>([]);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [openAddDialog, setOpenAddDialog] = useState(false);
  const [addForm, setAddForm] = useState({ id: "", name: "", password: "" });
  const [openEditDialog, setOpenEditDialog] = useState(false);
  const [isEditName, setIsEditName] = useState(false);
  const [isEditPassword, setIsEditPassword] = useState(false);
  const [editName, setEditName] = useState("");
  const [editPassword, setEditPassword] = useState("");
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);

  const handleAddSubmit = async () => {
    if (!addForm.id || !addForm.name || !addForm.password) {
      notify("Please fill in all required fields correctly");
      return;
    }
    if (!userInfo?.token) {
      notify("Admin token is required");
      return;
    }
    const result = await UserService.createUser({
      adminToken: userInfo.token,
      userId: addForm.id,
      name: addForm.name,
      password: CryptoJS.SHA256(addForm.password).toString(),
      role: tabs[selected].role,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("User added successfully", "success");
      setOpenAddDialog(false);
      setAddForm({ id: "", name: "", password: "" });
      toggleFlag();
    }
  };

  const handleEditNameSubmit = async () => {
    if (!editName) {
      notify("Please enter a valid name");
      return;
    }
    if (!selectedUser) {
      notify("No user selected");
      return;
    }
    if (!userInfo?.token) {
      notify("Admin token is required");
      return;
    }
    const result = await UserService.updateUserName({
      adminToken: userInfo.token,
      userId: selectedUser.id,
      name: isEditName ? editName : selectedUser.name,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("User name updated successfully", "success");
      setSelectedUser({ ...selectedUser, name: editName });
      setIsEditName(false);
      toggleFlag();
    }
  };

  const handleEditPasswordSubmit = async () => {
    if (!editPassword) {
      notify("Please enter a valid password");
      return;
    }
    if (!selectedUser) {
      notify("No user selected");
      return;
    }
    if (!userInfo?.token) {
      notify("Admin token is required");
      return;
    }
    const result = await UserService.updateUserPassword({
      adminToken: userInfo.token,
      userId: selectedUser.id,
      password: CryptoJS.SHA256(editPassword).toString(),
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("User password updated successfully", "success");
      setIsEditPassword(false);
    }
  };

  const handleDeleteUser = async () => {
    if (!selectedUser || !userInfo?.token) {
      notify("No user selected or admin token is missing");
      return;
    }
    const result = await UserService.deleteUser({
      adminToken: userInfo.token,
      userId: selectedUser.id,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("User deleted successfully", "success");
      setOpenDeleteDialog(false);
      setSelectedUser(null);
      toggleFlag();
    }
  };

  useEffect(() => {
    UserService.queryUsers({ role: tabs[selected].role }).then((result) => {
      if (result instanceof Error) notify(result.message);
      else setUserList(result);
    });
  }, [flag, notify, selected]);

  return (
    <Stack display="flex" flex={1}>
      <Box display="flex" position="sticky" p={2}>
        <Tabs
          value={selected}
          onChange={(_, newValue) => setSelected(newValue)}
          sx={{ flex: 1 }}
        >
          {tabs.map((tab, index) => (
            <Tab key={index} label={tab.label} value={index} />
          ))}
        </Tabs>

        <IconButton onClick={() => setOpenAddDialog(true)}>
          <Add />
        </IconButton>
      </Box>

      <Grid container maxWidth="lg" p={4} spacing={2} alignContent="center">
        {userList.map((user) => (
          <Grid key={user.id} size="auto">
            <Card variant="outlined">
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  {user.name}
                </Typography>

                <Typography variant="body2" color="text.secondary">
                  ID: {user.id}
                </Typography>
              </CardContent>

              <CardActions>
                <IconButton
                  onClick={() => {
                    setSelectedUser(user);
                    setOpenEditDialog(true);
                  }}
                >
                  <Edit />
                </IconButton>

                <IconButton
                  onClick={() => {
                    setSelectedUser(user);
                    setOpenDeleteDialog(true);
                  }}
                >
                  <Delete />
                </IconButton>
              </CardActions>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Dialog
        open={openAddDialog}
        onClose={() => setOpenAddDialog(false)}
        fullWidth
      >
        <DialogTitle>添加新用户</DialogTitle>

        <DialogContent>
          <Stack spacing={2} p={2}>
            <TextField
              autoFocus
              required
              label="用户ID"
              name="id"
              value={addForm.id}
              onChange={(e) =>
                setAddForm((prev) => ({ ...prev, id: e.target.value }))
              }
            />

            <TextField
              required
              label="姓名"
              name="name"
              value={addForm.name}
              onChange={(e) =>
                setAddForm((prev) => ({ ...prev, name: e.target.value }))
              }
            />

            <TextField
              required
              label="密码"
              name="password"
              type="password"
              value={addForm.password}
              onChange={(e) =>
                setAddForm((prev) => ({ ...prev, password: e.target.value }))
              }
            />
          </Stack>
        </DialogContent>

        <DialogActions>
          <Button onClick={() => setOpenAddDialog(false)} variant="text">
            取消
          </Button>

          <Button onClick={handleAddSubmit} variant="contained">
            添加用户
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog
        open={openEditDialog}
        onClose={() => setOpenEditDialog(false)}
        fullWidth
      >
        <DialogTitle>编辑用户信息</DialogTitle>

        <DialogContent>
          <Stack spacing={2} p={2}>
            <Box display="flex">
              <Typography variant="body1" m={2}>
                姓名
              </Typography>

              {isEditName ? (
                <>
                  <TextField
                    autoFocus
                    label="姓名"
                    name="name"
                    value={editName}
                    onChange={(e) => setEditName(e.target.value)}
                    sx={{ flex: 1, mr: 2 }}
                  />

                  <Button variant="contained" onClick={handleEditNameSubmit}>
                    确定
                  </Button>
                </>
              ) : (
                <>
                  <Typography variant="body1" m={2}>
                    {selectedUser?.name || "未设置"}
                  </Typography>

                  <Button
                    variant="text"
                    onClick={() => {
                      setIsEditName(true);
                      setEditName(selectedUser?.name || "");
                    }}
                  >
                    编辑
                  </Button>
                </>
              )}
            </Box>

            <Box display="flex">
              <Typography variant="body1" m={2}>
                密码
              </Typography>

              {isEditPassword ? (
                <>
                  <TextField
                    autoFocus
                    label="密码"
                    name="password"
                    type="password"
                    value={editPassword}
                    onChange={(e) => setEditPassword(e.target.value)}
                    sx={{ flex: 1, mr: 2 }}
                  />

                  <Button
                    variant="contained"
                    onClick={handleEditPasswordSubmit}
                  >
                    确定
                  </Button>
                </>
              ) : (
                <Button
                  variant="text"
                  onClick={() => {
                    setIsEditPassword(true);
                    setEditPassword("");
                  }}
                >
                  编辑
                </Button>
              )}
            </Box>
          </Stack>
        </DialogContent>

        <DialogActions>
          <Button
            onClick={() => {
              setOpenEditDialog(false);
              setIsEditName(false);
              setIsEditPassword(false);
            }}
            variant="contained"
          >
            取消
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog
        open={openDeleteDialog}
        onClose={() => setOpenDeleteDialog(false)}
        fullWidth
      >
        <DialogTitle>删除用户</DialogTitle>

        <DialogContent>
          <Typography variant="body1" m={2}>
            确认删除用户 {selectedUser?.name} ({selectedUser?.id}) 吗？
          </Typography>
        </DialogContent>

        <DialogActions>
          <Button onClick={() => setOpenDeleteDialog(false)} variant="text">
            取消
          </Button>

          <Button onClick={handleDeleteUser} variant="text" color="error">
            确定
          </Button>
        </DialogActions>
      </Dialog>
    </Stack>
  );
}
