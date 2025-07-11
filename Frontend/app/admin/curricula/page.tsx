"use client";

import { useAuth } from "@/context/authContext";
import { useNotification } from "@/context/notificationContext";
import CurriculumService from "@/services/curriculumService";
import RegisterService from "@/services/registerService";
import { Recommendation, Subject } from "@/types/curriculumService";
import { Department, Stage, StageMap } from "@/types/registerService";
import { Add, Delete } from "@mui/icons-material";
import {
  Box,
  Button,
  Card,
  CardActionArea,
  CardContent,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Grid,
  IconButton,
  MenuItem,
  Select,
  Stack,
  Tab,
  Tabs,
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
  const [subjectList, setSubjectList] = useState<Subject[]>([]);
  const [openDialog, setOpenDialog] = useState(false);
  const [form, setForm] = useState({
    subjectId: "",
    priority: NaN,
  });
  const [selectedRecommendationId, setSelectedRecommendationId] = useState<
    string | null
  >(null);
  const [selectedStage, setSelectedStage] = useState(Stage.Freshman_0);
  const [recommendationList, setRecommendationList] = useState<
    Recommendation[]
  >([]);
  const [isAdding, setIsAdding] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);

  const handleAddSubmit = async () => {
    if (!form || !form.subjectId || !form.priority || form.priority <= 0) {
      notify("Please fill in all fields correctly");
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
    const result = await CurriculumService.createRecommendation({
      adminToken: userInfo.token,
      departmentId: selectedDepartment.id,
      stage: selectedStage,
      ...form,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("Recommendation added successfully", "success");
      setIsAdding(false);
      setForm({
        subjectId: "",
        priority: NaN,
      });
      toggleFlag();
    }
  };

  const handleDeleteSubmit = async () => {
    if (!selectedRecommendationId) {
      notify("No recommendation selected");
      return;
    }
    if (!userInfo?.token) {
      notify("Admin token is required");
      return;
    }
    const result = await CurriculumService.deleteRecommendation({
      adminToken: userInfo.token,
      recommendationId: selectedRecommendationId,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("Recommendation deleted successfully", "success");
      setIsDeleting(false);
      setSelectedRecommendationId(null);
      toggleFlag();
    }
  };

  useEffect(() => {
    RegisterService.queryDepartments({ child: undefined }).then((result) => {
      if (result instanceof Error) notify(result.message);
      else setDepartmentList(result);
    });
    CurriculumService.querySubjects({ child: undefined }).then((result) => {
      if (result instanceof Error) notify(result.message);
      else setSubjectList(result);
    });
  }, [flag, notify]);

  useEffect(() => {
    if (!selectedDepartment || !selectedStage) return;
    CurriculumService.queryRecommendations({
      departmentId: selectedDepartment.id,
      stage: selectedStage,
    }).then((result) => {
      if (result instanceof Error) notify(result.message);
      else setRecommendationList(result);
    });
  }, [flag, notify, selectedDepartment, selectedStage]);

  return (
    <Stack display="flex" flex={1}>
      <Grid container maxWidth="lg" p={4} spacing={2} alignContent="center">
        {departmentList.map((department) => (
          <Grid key={department.id} size="auto">
            <Card variant="outlined">
              <CardActionArea
                onClick={() => {
                  setSelectedDepartment(department);
                  setOpenDialog(true);
                }}
              >
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    {department.name}
                  </Typography>
                </CardContent>
              </CardActionArea>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} fullWidth>
        <DialogTitle>{selectedDepartment?.name || "院系"} 培养方案</DialogTitle>

        <DialogContent>
          <Stack spacing={2} p={2}>
            <Box display="flex" alignContent="center">
              <Tabs
                variant="scrollable"
                scrollButtons="auto"
                value={selectedStage}
                onChange={(_, newValue) => setSelectedStage(newValue)}
              >
                {Object.values(Stage).map((stage) => (
                  <Tab key={stage} label={StageMap[stage]} value={stage} />
                ))}
              </Tabs>

              <IconButton
                onClick={() => {
                  setIsDeleting(false);
                  setIsAdding(true);
                }}
              >
                <Add />
              </IconButton>
            </Box>

            {recommendationList.length
              ? recommendationList.map((recommendation) => (
                  <Box
                    key={recommendation.id}
                    display="flex"
                    alignItems="center"
                  >
                    <Typography flex={1} variant="body2" mr={2}>
                      {recommendation.subject.name} - 优先级{" "}
                      {recommendation.priority}
                    </Typography>

                    {isDeleting &&
                    selectedRecommendationId === recommendation.id ? (
                      <Button
                        onClick={handleDeleteSubmit}
                        variant="text"
                        color="error"
                      >
                        确认删除
                      </Button>
                    ) : (
                      <IconButton
                        onClick={() => {
                          setSelectedRecommendationId(recommendation.id);
                          setIsDeleting(true);
                        }}
                      >
                        <Delete />
                      </IconButton>
                    )}
                  </Box>
                ))
              : !isAdding && (
                  <Typography
                    flex={1}
                    variant="body2"
                    color="textSecondary"
                    mr={2}
                    alignContent={"center"}
                  >
                    无记录
                  </Typography>
                )}

            {isAdding && (
              <Stack spacing={1} alignContent="center">
                <Select
                  label="科目"
                  value={form.subjectId}
                  onChange={(e) =>
                    setForm((prev) => ({
                      ...prev,
                      subjectId: e.target.value,
                    }))
                  }
                >
                  {subjectList.map((subject) => (
                    <MenuItem key={subject.id} value={subject.id}>
                      {subject.name}
                    </MenuItem>
                  ))}
                </Select>

                <TextField
                  type="number"
                  label="优先级"
                  value={form.priority ? form.priority : ""}
                  onChange={(e) =>
                    setForm((prev) => ({
                      ...prev,
                      priority: Number(e.target.value),
                    }))
                  }
                />

                <Button onClick={handleAddSubmit} variant="contained">
                  添加
                </Button>
              </Stack>
            )}
          </Stack>
        </DialogContent>

        <DialogActions>
          <Button onClick={() => setOpenDialog(false)} variant="text">
            确定
          </Button>
        </DialogActions>
      </Dialog>
    </Stack>
  );
}
