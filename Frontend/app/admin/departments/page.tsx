"use client";

import { useAuth } from "@/context/authContext";
import { useNotification } from "@/context/notificationContext";
import RegisterService from "@/services/registerService";
import { Department } from "@/types/registerService";
import { Add, Edit, Delete } from "@mui/icons-material";
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
  TextField,
  Typography,
} from "@mui/material";
import { useEffect, useState } from "react";

export default function AdminDepartmentsPage() {
  const { notify } = useNotification();
  const { userInfo } = useAuth();
  const [flag, setFlag] = useState(false);
  const toggleFlag = () => setFlag((prev) => !prev);
  const [departmentList, setDepartmentList] = useState<Department[]>([]);
  const [selectedDepartment, setSelectedDepartment] =
    useState<Department | null>(null);
  const [openAddDialog, setOpenAddDialog] = useState(false);
  const [addForm, setAddForm] = useState({ name: "" });
  const [openEditDialog, setOpenEditDialog] = useState(false);
  const [editForm, setEditForm] = useState({ name: "" });
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);

  const handleAddSubmit = async () => {
    if (!addForm.name) {
      notify("Please enter a valid department name");
      return;
    }
    if (!userInfo?.token) {
      notify("Admin token is required");
      return;
    }
    const result = await RegisterService.createDepartment({
      adminToken: userInfo.token,
      name: addForm.name,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("Department added successfully", "success");
      setOpenAddDialog(false);
      setAddForm({ name: "" });
      toggleFlag();
    }
  };

  const handleEditSubmit = async () => {
    if (!editForm.name) {
      notify("Please enter a valid department name");
      return;
    }
    if (!selectedDepartment) {
      notify("No department selected");
      return;
    }
    if (!userInfo?.token) {
      notify("Admin token is required");
      return;
    }
    const result = await RegisterService.updateDepartment({
      adminToken: userInfo.token,
      departmentId: selectedDepartment.id,
      name: editForm.name,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("Department updated successfully", "success");
      setOpenEditDialog(false);
      toggleFlag();
    }
  };

  const handleDeleteSubmit = async () => {
    if (!selectedDepartment) {
      notify("No department selected");
      return;
    }
    if (!userInfo?.token) {
      notify("Admin token is required");
      return;
    }
    const result = await RegisterService.deleteDepartment({
      adminToken: userInfo.token,
      departmentId: selectedDepartment.id,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("Department deleted successfully", "success");
      setOpenDeleteDialog(false);
      toggleFlag();
    }
  };

  useEffect(() => {
    RegisterService.queryDepartments({ child: undefined }).then((result) => {
      if (result instanceof Error) notify(result.message);
      else setDepartmentList(result);
    });
  }, [flag, notify]);

  return (
    <Stack display="flex" flex={1}>
      <Box display="flex" position="sticky" p={2}>
        <Box flex={1} />
        <IconButton onClick={() => setOpenAddDialog(true)}>
          <Add />
        </IconButton>
      </Box>

      <Grid container maxWidth="lg" p={4} spacing={2} alignContent="center">
        {departmentList.map((department) => (
          <Grid key={department.id} size="auto">
            <Card variant="outlined">
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  {department.name}
                </Typography>
              </CardContent>

              <CardActions>
                <IconButton
                  onClick={() => {
                    setSelectedDepartment(department);
                    setEditForm({ name: department.name });
                    setOpenEditDialog(true);
                  }}
                >
                  <Edit />
                </IconButton>

                <IconButton
                  onClick={() => {
                    setSelectedDepartment(department);
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
        <DialogTitle>添加新院系</DialogTitle>

        <DialogContent>
          <Stack spacing={2} p={2}>
            <TextField
              autoFocus
              required
              label="名称"
              value={addForm.name}
              onChange={(e) => setAddForm({ name: e.target.value })}
            />
          </Stack>
        </DialogContent>

        <DialogActions>
          <Button onClick={() => setOpenAddDialog(false)} variant="text">
            取消
          </Button>

          <Button onClick={handleAddSubmit} variant="contained">
            添加院系
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog
        open={openEditDialog}
        onClose={() => setOpenEditDialog(false)}
        fullWidth
      >
        <DialogTitle>编辑院系信息</DialogTitle>
        <DialogContent>
          <Stack spacing={2} p={2}>
            <TextField
              autoFocus
              required
              label="名称"
              value={editForm.name}
              onChange={(e) => setEditForm({ name: e.target.value })}
            />
          </Stack>
        </DialogContent>

        <DialogActions>
          <Button onClick={() => setOpenEditDialog(false)} variant="text">
            取消
          </Button>

          <Button onClick={handleEditSubmit} variant="contained">
            确定
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog
        open={openDeleteDialog}
        onClose={() => setOpenDeleteDialog(false)}
        fullWidth
      >
        <DialogTitle>删除院系</DialogTitle>

        <DialogContent>
          <Typography>
            确定要删除院系 {selectedDepartment?.name} 吗？
          </Typography>
        </DialogContent>

        <DialogActions>
          <Button onClick={() => setOpenDeleteDialog(false)} variant="text">
            取消
          </Button>

          <Button onClick={handleDeleteSubmit} variant="text" color="error">
            删除
          </Button>
        </DialogActions>
      </Dialog>
    </Stack>
  );
}
