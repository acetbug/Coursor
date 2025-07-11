"use client";

import { useAuth } from "@/context/authContext";
import { useNotification } from "@/context/notificationContext";
import CurriculumService from "@/services/curriculumService";
import { Subject } from "@/types/curriculumService";
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

export default function AdminSubjectsPage() {
  const { notify } = useNotification();
  const { userInfo } = useAuth();
  const [flag, setFlag] = useState(false);
  const toggleFlag = () => setFlag((prev) => !prev);
  const [subjectList, setSubjectList] = useState<Subject[]>([]);
  const [selectedSubject, setSelectedSubject] = useState<Subject | null>(null);
  const [openAddDialog, setOpenAddDialog] = useState(false);
  const [addForm, setAddForm] = useState({ name: "", credits: NaN });
  const [openEditDialog, setOpenEditDialog] = useState(false);
  const [editForm, setEditForm] = useState({ name: "", credits: NaN });
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);

  const handleAddSubmit = async () => {
    if (
      !addForm.name ||
      !Number.isInteger(addForm.credits) ||
      addForm.credits <= 0
    ) {
      notify("Please enter all the fields correctly");
      return;
    }
    if (!userInfo?.token) {
      notify("Admin token is required");
      return;
    }
    const result = await CurriculumService.createSubject({
      adminToken: userInfo.token,
      name: addForm.name,
      credits: addForm.credits,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("Subject added successfully", "success");
      setOpenAddDialog(false);
      setAddForm({ name: "", credits: 0 });
      toggleFlag();
    }
  };

  const handleEditSubmit = async () => {
    if (
      !editForm.name ||
      !Number.isInteger(editForm.credits) ||
      editForm.credits <= 0
    ) {
      notify("Please enter all the fields correctly");
      return;
    }
    if (!selectedSubject) {
      notify("No subject selected");
      return;
    }
    if (!userInfo?.token) {
      notify("Admin token is required");
      return;
    }
    const result = await CurriculumService.updateSubject({
      adminToken: userInfo.token,
      subjectId: selectedSubject.id,
      name: editForm.name,
      credits: editForm.credits,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("Subject updated successfully", "success");
      setOpenEditDialog(false);
      toggleFlag();
    }
  };

  const handleDeleteSubmit = async () => {
    if (!selectedSubject) {
      notify("No subject selected");
      return;
    }
    if (!userInfo?.token) {
      notify("Admin token is required");
      return;
    }
    const result = await CurriculumService.deleteSubject({
      adminToken: userInfo.token,
      subjectId: selectedSubject.id,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("Subject deleted successfully", "success");
      setOpenDeleteDialog(false);
      toggleFlag();
    }
  };

  useEffect(() => {
    CurriculumService.querySubjects({ child: undefined }).then((result) => {
      if (result instanceof Error) notify(result.message);
      else setSubjectList(result);
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
        {subjectList.map((subject) => (
          <Grid key={subject.id} size="auto">
            <Card variant="outlined">
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  {subject.name}
                </Typography>

                <Typography variant="body2" color="textSecondary">
                  学分: {subject.credits}
                </Typography>
              </CardContent>

              <CardActions>
                <IconButton
                  onClick={() => {
                    setSelectedSubject(subject);
                    setEditForm({ ...subject });
                    setOpenEditDialog(true);
                  }}
                >
                  <Edit />
                </IconButton>

                <IconButton
                  onClick={() => {
                    setSelectedSubject(subject);
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
        <DialogTitle>添加新科目</DialogTitle>

        <DialogContent>
          <Stack spacing={2} p={2}>
            <TextField
              autoFocus
              required
              label="名称"
              value={addForm.name}
              onChange={(e) =>
                setAddForm((prev) => ({ ...prev, name: e.target.value }))
              }
            />

            <TextField
              required
              label="学分"
              type="number"
              value={addForm.credits ? addForm.credits : ""}
              onChange={(e) =>
                setAddForm((prev) => ({
                  ...prev,
                  credits: Number(e.target.value),
                }))
              }
            />
          </Stack>
        </DialogContent>

        <DialogActions>
          <Button onClick={() => setOpenAddDialog(false)} variant="text">
            取消
          </Button>

          <Button onClick={handleAddSubmit} variant="contained">
            添加科目
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog
        open={openEditDialog}
        onClose={() => setOpenEditDialog(false)}
        fullWidth
      >
        <DialogTitle>编辑科目信息</DialogTitle>
        <DialogContent>
          <Stack spacing={2} p={2}>
            <TextField
              autoFocus
              required
              label="名称"
              value={editForm.name}
              onChange={(e) =>
                setEditForm((prev) => ({ ...prev, name: e.target.value }))
              }
            />

            <TextField
              required
              label="学分"
              type="number"
              value={editForm.credits ? editForm.credits : ""}
              onChange={(e) =>
                setEditForm((prev) => ({
                  ...prev,
                  credits: Number(e.target.value),
                }))
              }
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
        <DialogTitle>删除科目</DialogTitle>

        <DialogContent>
          <Typography>确定要删除科目 {selectedSubject?.name} 吗？</Typography>
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
