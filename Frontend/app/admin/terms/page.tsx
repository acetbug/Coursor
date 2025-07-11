"use client";

import { useAuth } from "@/context/authContext";
import { useNotification } from "@/context/notificationContext";
import EnrollmentService from "@/services/enrollmentService";
import RegisterService from "@/services/registerService";
import { Phase, Term } from "@/types/registerService";
import { Add, Edit, Delete, PlayArrow } from "@mui/icons-material";
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

export default function AdminTermsPage() {
  const { notify } = useNotification();
  const { userInfo } = useAuth();
  const [flag, setFlag] = useState(false);
  const toggleFlag = () => setFlag((prev) => !prev);
  const [termList, setTermList] = useState<Term[]>([]);
  const [selectedTerm, setSelectedTerm] = useState<Term | null>(null);
  const [openAddDialog, setOpenAddDialog] = useState(false);
  const [addForm, setAddForm] = useState({ name: "" });
  const [openEditDialog, setOpenEditDialog] = useState(false);
  const [editForm, setEditForm] = useState({ name: "" });
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [openExecuteDialog, setOpenExecuteDialog] = useState(false);

  const handleAddSubmit = async () => {
    if (!addForm.name) {
      notify("Please enter a valid term name");
      return;
    }
    if (!userInfo?.token) {
      notify("Admin token is required");
      return;
    }
    const result = await RegisterService.createTerm({
      adminToken: userInfo.token,
      name: addForm.name,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("Term added successfully", "success");
      setOpenAddDialog(false);
      setAddForm({ name: "" });
      toggleFlag();
    }
  };

  const handleEditSubmit = async () => {
    if (!editForm.name) {
      notify("Please enter a valid term name");
      return;
    }
    if (!selectedTerm) {
      notify("No term selected");
      return;
    }
    if (!userInfo?.token) {
      notify("Admin token is required");
      return;
    }
    const result = await RegisterService.updateTerm({
      adminToken: userInfo.token,
      termId: selectedTerm.id,
      name: editForm.name,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("Term updated successfully", "success");
      setOpenEditDialog(false);
      toggleFlag();
    }
  };

  const handleDeleteSubmit = async () => {
    if (!selectedTerm) {
      notify("No term selected");
      return;
    }
    if (!userInfo?.token) {
      notify("Admin token is required");
      return;
    }
    const result = await RegisterService.deleteTerm({
      adminToken: userInfo.token,
      termId: selectedTerm.id,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("Term deleted successfully", "success");
      setOpenDeleteDialog(false);
      toggleFlag();
    }
  };

  const handleExecuteSubmit = async () => {
    if (!selectedTerm) {
      notify("No term selected");
      return;
    }
    if (!userInfo?.token) {
      notify("Admin token is required");
      return;
    }
    const result = await EnrollmentService.executeEnrollment({
      adminToken: userInfo.token,
      termId: selectedTerm.id,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("Enrollment executed successfully", "success");
      setOpenExecuteDialog(false);
      toggleFlag();
    }
  };

  useEffect(() => {
    RegisterService.queryTerms({ child: undefined }).then((result) => {
      if (result instanceof Error) notify(result.message);
      else setTermList(result);
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
        {termList.map((term) => (
          <Grid key={term.id} size="auto">
            <Card variant="outlined">
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  {term.name}
                </Typography>
              </CardContent>

              <CardActions>
                <IconButton
                  onClick={() => {
                    setSelectedTerm(term);
                    setEditForm({ name: term.name });
                    setOpenEditDialog(true);
                  }}
                >
                  <Edit />
                </IconButton>

                <IconButton
                  onClick={() => {
                    setSelectedTerm(term);
                    setOpenDeleteDialog(true);
                  }}
                >
                  <Delete />
                </IconButton>

                {term.phase === Phase.Enrolling && (
                  <IconButton
                    onClick={() => {
                      setSelectedTerm(term);
                      setOpenExecuteDialog(true);
                    }}
                  >
                    <PlayArrow />
                  </IconButton>
                )}
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
        <DialogTitle>添加新学期</DialogTitle>

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
            添加学期
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog
        open={openEditDialog}
        onClose={() => setOpenEditDialog(false)}
        fullWidth
      >
        <DialogTitle>编辑学期信息</DialogTitle>

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
        <DialogTitle>删除学期</DialogTitle>

        <DialogContent>
          <Typography>确定要删除学期 {selectedTerm?.name} 吗？</Typography>
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

      <Dialog
        open={openExecuteDialog}
        onClose={() => setOpenExecuteDialog(false)}
        fullWidth
      >
        <DialogTitle>执行选课</DialogTitle>

        <DialogContent>
          <Typography>
            确定要执行学期 {selectedTerm?.name} 的选课吗？
          </Typography>
        </DialogContent>

        <DialogActions>
          <Button onClick={() => setOpenExecuteDialog(false)} variant="text">
            取消
          </Button>

          <Button onClick={handleExecuteSubmit} variant="contained">
            执行选课
          </Button>
        </DialogActions>
      </Dialog>
    </Stack>
  );
}
