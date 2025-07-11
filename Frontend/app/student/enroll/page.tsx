"use client";

import { useAuth } from "@/context/authContext";
import { useNotification } from "@/context/notificationContext";
import CurriculumService from "@/services/curriculumService";
import EnrollmentService from "@/services/enrollmentService";
import RegisterService from "@/services/registerService";
import { Course } from "@/types/curriculumService";
import { Enrollment } from "@/types/enrollmentService";
import { Phase, Student, Trace } from "@/types/registerService";
import {
  Stack,
  Box,
  Tabs,
  Tab,
  Typography,
  Grid,
  Card,
  CardContent,
  CardActionArea,
  Dialog,
  DialogTitle,
  Slider,
  DialogContent,
  Button,
} from "@mui/material";
import { useEffect, useState } from "react";

export default function StudentEnrollPage() {
  const { notify } = useNotification();
  const { userInfo } = useAuth();
  const [flag, setFlag] = useState(false);
  const toggleFlag = () => setFlag((prev) => !prev);
  const [student, setStudent] = useState<Student | null>(null);
  const [selectedTrace, setSelectedTrace] = useState<Trace | null>(null);
  const [selectedEnrollments, setSelectedEnrollments] = useState<Enrollment[]>(
    []
  );
  const [selectedEnrollment, setSelectedEnrollment] =
    useState<Enrollment | null>(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [isEditingStake, setIsEditingStake] = useState(false);
  const [stake, setStake] = useState(0);
  const [courseList, setCourseList] = useState<Course[]>([]);
  const [selectedCourseId, setSelectedCourseId] = useState<string | null>(null);
  const [isAddingSelection, setIsAddingSelection] = useState(false);
  const [isEditingSelection, setIsEditingSelection] = useState(false);

  const handleEditStake = async () => {
    if (stake < 0 || stake > 100) {
      notify("Stake must be between 0 and 100");
      return;
    }
    if (!selectedEnrollment?.selection) {
      notify("No enrollment selected");
      return;
    }
    if (!selectedTrace) {
      notify("No trace selected");
      return;
    }
    if (!userInfo?.token) {
      notify("Student token is required");
      return;
    }
    const result = await EnrollmentService.updateStake({
      studentToken: userInfo.token,
      termId: selectedTrace.term.id,
      selectionId: selectedEnrollment.selection.id,
      points: stake,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("Stake updated successfully", "success");
      setOpenDialog(false);
      setIsEditingStake(false);
      toggleFlag();
    }
  };

  const handleAddSelection = async () => {
    if (!selectedTrace || !selectedEnrollment || !selectedCourseId) {
      notify("Please select a trace, an enrollment, and a course");
      return;
    }
    if (!userInfo?.token) {
      notify("Student token is required");
      return;
    }
    const result = await EnrollmentService.createSelection({
      studentToken: userInfo.token,
      termId: selectedTrace.term.id,
      subjectId: selectedEnrollment.subject.id,
      courseId: selectedCourseId,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("Selection added successfully", "success");
      setIsAddingSelection(false);
      setOpenDialog(false);
      setSelectedCourseId(null);
      toggleFlag();
    }
  };

  const handleEditSelection = async () => {
    if (!selectedTrace || !selectedEnrollment?.selection || !selectedCourseId) {
      notify("Please select a trace, an enrollment selection, and a course");
      return;
    }
    if (!userInfo?.token) {
      notify("Student token is required");
      return;
    }
    const result = await EnrollmentService.updateSelectedCourse({
      studentToken: userInfo.token,
      termId: selectedTrace.term.id,
      selectionId: selectedEnrollment.selection.id,
      courseId: selectedCourseId,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("Selection updated successfully", "success");
      setIsEditingSelection(false);
      setSelectedCourseId(null);
      toggleFlag();
    }
  };

  useEffect(() => {
    if (!userInfo?.token) return;
    RegisterService.queryStudent({ studentId: userInfo.id }).then((result) => {
      if (result instanceof Error) notify(result.message);
      else {
        setStudent(result);
        setSelectedTrace(
          result.traces.find((trace) => trace.term.phase === Phase.Enrolling) ||
            null
        );
      }
    });
  }, [flag, notify, userInfo]);

  useEffect(() => {
    if (!selectedTrace || !userInfo?.id || !student?.department) return;
    CurriculumService.queryRecommendations({
      departmentId: student.department.id,
      stage: selectedTrace.stage,
    }).then((result) => {
      if (result instanceof Error) notify(result.message);
      else
        EnrollmentService.querySelections({
          studentId: userInfo.id,
          termId: selectedTrace.term.id,
        }).then((selections) => {
          if (selections instanceof Error) notify(selections.message);
          else
            setSelectedEnrollments(
              result.map((rec) => ({
                subject: rec.subject,
                selection: selections.find(
                  (selection) => selection.subjectId === rec.subject.id
                ),
              }))
            );
        });
    });
  }, [flag, notify, selectedTrace, student, userInfo]);

  useEffect(() => {
    if (!selectedTrace || !selectedEnrollment) return;
    CurriculumService.queryCourses({
      subjectId: selectedEnrollment.subject.id,
      termId: selectedTrace.term.id,
    }).then((result) => {
      if (result instanceof Error) notify(result.message);
      else setCourseList(result);
    });
  }, [flag, notify, selectedEnrollment, selectedTrace]);

  return student?.traces.length ? (
    <Stack display="flex" flex={1} spacing={2} p={2}>
      <Tabs
        variant="scrollable"
        scrollButtons="auto"
        value={selectedTrace || student.traces[0]}
        onChange={(_, newValue) => setSelectedTrace(newValue)}
      >
        {student.traces.map((trace) => (
          <Tab key={trace.term.id} label={trace.term.name} value={trace} />
        ))}
      </Tabs>

      {student.department ? (
        selectedEnrollments.length && (
          <Grid container p={2} maxWidth="lg" spacing={2} alignContent="center">
            {selectedEnrollments.map((enrollment) => (
              <Grid key={enrollment.subject.id} size="auto">
                <Card
                  variant={enrollment.selection ? "outlined" : "elevation"}
                  sx={{
                    bgcolor: enrollment.selection ? "primary.dark" : "inherit",
                    color: enrollment.selection ? "white" : "inherit",
                  }}
                >
                  <CardActionArea
                    onClick={() => {
                      setSelectedEnrollment(enrollment);
                      setOpenDialog(true);
                    }}
                  >
                    <CardContent>
                      <Typography variant="h6">
                        {enrollment.subject.name}
                      </Typography>

                      <Typography
                        variant="body2"
                        color={
                          enrollment.selection ? "white" : "text.secondary"
                        }
                      >
                        {enrollment.subject.credits} 学分
                      </Typography>
                    </CardContent>
                  </CardActionArea>
                </Card>
              </Grid>
            ))}
          </Grid>
        )
      ) : (
        <Box
          display="flex"
          justifyContent="center"
          alignItems="center"
          flex={1}
        >
          <Typography variant="body1" color="text.secondary">
            暂无学籍数据
          </Typography>
        </Box>
      )}

      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} fullWidth>
        <DialogTitle>
          选择 {selectedEnrollment ? selectedEnrollment.subject.name : ""}{" "}
          科目的课程
        </DialogTitle>

        <DialogContent>
          <Stack spacing={2} p={2}>
            {selectedEnrollment?.selection && (
              <>
                <Slider
                  value={
                    isEditingStake ? stake : selectedEnrollment.selection.points
                  }
                  min={0}
                  max={100}
                  step={1}
                  onChange={(_, newValue) => {
                    setStake(newValue);
                    setIsEditingStake(true);
                  }}
                />

                {isEditingStake && (
                  <Button variant="contained" onClick={handleEditStake}>
                    确认修改
                  </Button>
                )}
              </>
            )}

            {courseList.length ? (
              <>
                {courseList.map((course) => (
                  <Card
                    key={course.id}
                    variant={
                      selectedEnrollment?.selection?.courseId === course.id ||
                      selectedCourseId === course.id
                        ? "outlined"
                        : "elevation"
                    }
                    sx={{
                      bgcolor:
                        selectedEnrollment?.selection?.courseId === course.id
                          ? "primary.dark"
                          : "inherit",
                    }}
                  >
                    <CardActionArea
                      onClick={() => {
                        if (
                          course.id === selectedEnrollment?.selection?.courseId
                        )
                          return;
                        setSelectedCourseId(course.id);
                        if (selectedEnrollment?.selection)
                          setIsEditingSelection(true);
                        else setIsAddingSelection(true);
                      }}
                    >
                      <CardContent>
                        <Typography
                          variant="h6"
                          color={
                            selectedEnrollment?.selection?.courseId ===
                            course.id
                              ? "white"
                              : "inherit"
                          }
                        >
                          {course.teacher.name}
                        </Typography>

                        <Typography
                          variant="body2"
                          color={
                            selectedEnrollment?.selection?.courseId ===
                            course.id
                              ? "white"
                              : "text.secondary"
                          }
                        >
                          {course.schedule} @ {course.location}
                        </Typography>

                        <Typography
                          variant="body2"
                          color={
                            selectedEnrollment?.selection?.courseId ===
                            course.id
                              ? "white"
                              : "text.secondary"
                          }
                        >
                          课容量 {course.capacity} 人
                        </Typography>
                      </CardContent>
                    </CardActionArea>
                  </Card>
                ))}

                {isAddingSelection && (
                  <Button variant="contained" onClick={handleAddSelection}>
                    确认添加选课
                  </Button>
                )}

                {isEditingSelection && (
                  <Button variant="contained" onClick={handleEditSelection}>
                    确认修改选课
                  </Button>
                )}
              </>
            ) : (
              <Typography variant="body2" color="text.secondary">
                暂无课程数据
              </Typography>
            )}
          </Stack>
        </DialogContent>
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
