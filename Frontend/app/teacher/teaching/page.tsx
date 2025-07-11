"use client";

import { useAuth } from "@/context/authContext";
import { useNotification } from "@/context/notificationContext";
import CurriculumService from "@/services/curriculumService";
import RegisterService from "@/services/registerService";
import { Subject, Teaching } from "@/types/curriculumService";
import { Term } from "@/types/registerService";
import { Add, Delete, Edit } from "@mui/icons-material";
import {
  Stack,
  Box,
  Tabs,
  Tab,
  IconButton,
  Typography,
  Grid,
  Card,
  CardContent,
  CardActions,
  Dialog,
  DialogTitle,
  DialogContent,
  Select,
  TextField,
  MenuItem,
  DialogActions,
  Button,
} from "@mui/material";
import { useEffect, useState } from "react";

export default function TeacherTeachingPage() {
  const { notify } = useNotification();
  const { userInfo } = useAuth();
  const [flag, setFlag] = useState(false);
  const toggleFlag = () => setFlag((prev) => !prev);
  const [termList, setTermList] = useState<Term[]>([]);
  const [selectedTermId, setSelectedTermId] = useState<string | null>(null);
  const [subjectList, setSubjectList] = useState<Subject[]>([]);
  const [selectedSubjectId, setSelectedSubjectId] = useState<string | null>(
    null
  );
  const [teachingList, setTeachingList] = useState<Teaching[]>([]);
  const [openAddDialog, setOpenAddDialog] = useState(false);
  const [addForm, setAddForm] = useState({
    location: "",
    schedule: "",
    capacity: NaN,
  });
  const [openEditDialog, setOpenEditDialog] = useState(false);
  const [selectedLectureId, setSelectedLectureId] = useState<string | null>(
    null
  );
  const [editForm, setEditForm] = useState({
    location: "",
    schedule: "",
    capacity: 0,
  });
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);

  const handleAddSubmit = async () => {
    if (
      !addForm.location ||
      !addForm.schedule ||
      !Number.isInteger(addForm.capacity) ||
      addForm.capacity <= 0 ||
      !selectedSubjectId ||
      !selectedTermId
    ) {
      notify("Please enter all the fields correctly");
      return;
    }
    if (!userInfo?.token) {
      notify("Teacher token is required");
      return;
    }
    const result = await CurriculumService.createCourse({
      teacherToken: userInfo.token,
      subjectId: selectedSubjectId,
      termId: selectedTermId!,
      location: addForm.location,
      schedule: addForm.schedule,
      capacity: addForm.capacity,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("Course added successfully", "success");
      setOpenAddDialog(false);
      setAddForm({ location: "", schedule: "", capacity: NaN });
      toggleFlag();
    }
  };

  const handleEditSubmit = async () => {
    if (
      !editForm.location ||
      !editForm.schedule ||
      !Number.isInteger(editForm.capacity) ||
      editForm.capacity <= 0 ||
      !selectedLectureId
    ) {
      notify("Please enter all the fields correctly");
      return;
    }
    if (!userInfo?.token) {
      notify("Teacher token is required");
      return;
    }
    const result = await CurriculumService.updateCourse({
      teacherToken: userInfo.token,
      courseId: selectedLectureId,
      location: editForm.location,
      schedule: editForm.schedule,
      capacity: editForm.capacity,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("Course updated successfully", "success");
      setOpenEditDialog(false);
      toggleFlag();
    }
  };

  const handleDeleteSubmit = async () => {
    if (!selectedLectureId) {
      notify("No lecture selected");
      return;
    }
    if (!userInfo?.token) {
      notify("Teacher token is required");
      return;
    }
    const result = await CurriculumService.deleteCourse({
      teacherToken: userInfo.token,
      courseId: selectedLectureId,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("Course deleted successfully", "success");
      setOpenDeleteDialog(false);
      toggleFlag();
    }
  };

  useEffect(() => {
    RegisterService.queryTerms({ child: undefined }).then((result) => {
      if (result instanceof Error) notify(result.message);
      else {
        setTermList(result);
        if (result.length) setSelectedTermId(result[0].id);
      }
    });
    CurriculumService.querySubjects({ child: undefined }).then((result) => {
      if (result instanceof Error) notify(result.message);
      else setSubjectList(result);
    });
  }, [flag, notify]);

  useEffect(() => {
    if (!selectedTermId || !userInfo?.id) return;
    CurriculumService.queryTeachings({
      teacherId: userInfo.id,
      termId: selectedTermId,
    }).then((result) => {
      if (result instanceof Error) notify(result.message);
      else setTeachingList(result);
    });
  }, [flag, notify, selectedTermId, userInfo]);

  return termList.length ? (
    <Stack display="flex" flex={1} spacing={2}>
      <Box display="flex" position="sticky" p={2}>
        <Tabs
          variant="scrollable"
          scrollButtons="auto"
          value={selectedTermId || termList[0].id}
          onChange={(_, newValue) => setSelectedTermId(newValue)}
          sx={{ flex: 1 }}
        >
          {termList.map((term) => (
            <Tab key={term.id} label={term.name} value={term.id} />
          ))}
        </Tabs>

        <IconButton onClick={() => setOpenAddDialog(true)}>
          <Add />
        </IconButton>
      </Box>

      {teachingList.length ? (
        teachingList.map((teaching) => (
          <Stack key={teaching.subject.id} pl={4} pr={4} spacing={1}>
            <Typography variant="h6" color="primary">
              {teaching.subject.name}
            </Typography>

            <Grid container maxWidth="lg" spacing={2} alignContent="center">
              {teaching.lectures.map((lecture) => (
                <Grid key={lecture.id} size="auto">
                  <Card variant="outlined">
                    <CardContent>
                      <Typography variant="h6">{lecture.schedule}</Typography>

                      <Typography variant="body2" color="text.secondary">
                        @ {lecture.location}
                      </Typography>

                      <Typography variant="body2" color="text.secondary">
                        {lecture.capacity} 人
                      </Typography>
                    </CardContent>

                    <CardActions>
                      <IconButton
                        onClick={() => {
                          setSelectedLectureId(lecture.id);
                          setEditForm({
                            location: lecture.location,
                            schedule: lecture.schedule,
                            capacity: lecture.capacity,
                          });
                          setOpenEditDialog(true);
                        }}
                      >
                        <Edit />
                      </IconButton>

                      <IconButton
                        onClick={() => {
                          setSelectedLectureId(lecture.id);
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
          </Stack>
        ))
      ) : (
        <Typography variant="body1" color="text.secondary" align="center">
          暂无教学数据
        </Typography>
      )}

      <Dialog
        open={openAddDialog}
        onClose={() => setOpenAddDialog(false)}
        fullWidth
      >
        <DialogTitle>添加新课程</DialogTitle>

        <DialogContent>
          <Stack spacing={2} p={2}>
            <Select
              label="选择科目"
              value={selectedSubjectId || ""}
              onChange={(e) => setSelectedSubjectId(e.target.value || null)}
            >
              <MenuItem value="">
                <em>请选择科目</em>
              </MenuItem>
              {subjectList.map((subject) => (
                <MenuItem key={subject.id} value={subject.id}>
                  {subject.name}
                </MenuItem>
              ))}
            </Select>

            <TextField
              label="上课地点"
              value={addForm.location}
              onChange={(e) =>
                setAddForm({ ...addForm, location: e.target.value })
              }
            />

            <TextField
              label="上课时间"
              value={addForm.schedule}
              onChange={(e) =>
                setAddForm({ ...addForm, schedule: e.target.value })
              }
            />

            <TextField
              label="课程容量"
              type="number"
              value={addForm.capacity ? addForm.capacity : ""}
              onChange={(e) =>
                setAddForm({ ...addForm, capacity: Number(e.target.value) })
              }
            />
          </Stack>
        </DialogContent>

        <DialogActions>
          <Button onClick={() => setOpenAddDialog(false)} variant="text">
            取消
          </Button>

          <Button onClick={handleAddSubmit} variant="contained">
            添加课程
          </Button>
        </DialogActions>
      </Dialog>

      <Dialog
        open={openEditDialog}
        onClose={() => setOpenEditDialog(false)}
        fullWidth
      >
        <DialogTitle>编辑课程信息</DialogTitle>

        <DialogContent>
          <Stack spacing={2} p={2}>
            <TextField
              label="上课地点"
              value={editForm.location}
              onChange={(e) =>
                setEditForm({ ...editForm, location: e.target.value })
              }
            />

            <TextField
              label="上课时间"
              value={editForm.schedule}
              onChange={(e) =>
                setEditForm({ ...editForm, schedule: e.target.value })
              }
            />

            <TextField
              label="课程容量"
              type="number"
              value={editForm.capacity ? editForm.capacity : ""}
              onChange={(e) =>
                setEditForm({ ...editForm, capacity: Number(e.target.value) })
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
        <DialogTitle>删除课程</DialogTitle>

        <DialogContent>
          <Typography variant="body1">确定要删除该课程吗？</Typography>
        </DialogContent>

        <DialogActions>
          <Button onClick={() => setOpenDeleteDialog(false)} variant="text">
            取消
          </Button>

          <Button onClick={handleDeleteSubmit} variant="text" color="error">
            确定
          </Button>
        </DialogActions>
      </Dialog>
    </Stack>
  ) : (
    <Box display="flex" justifyContent="center" alignItems="center" flex={1}>
      <Typography variant="body1" color="text.secondary">
        暂无学期数据
      </Typography>
    </Box>
  );
}
