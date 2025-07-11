"use client";

import { useAuth } from "@/context/authContext";
import { useNotification } from "@/context/notificationContext";
import CurriculumService from "@/services/curriculumService";
import EnrollmentService from "@/services/enrollmentService";
import RegisterService from "@/services/registerService";
import { Comment, Course } from "@/types/curriculumService";
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
  const [commentList, setCommentList] = useState<Comment[]>([]);
  const [commentInput, setCommentInput] = useState("");
  const [isCommentLoading, setIsCommentLoading] = useState(false);

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

  const handleDeleteSelection = async () => {
    if (!selectedTrace || !selectedEnrollment?.selection) {
      notify("请选择要取消的选课");
      return;
    }
    if (!userInfo?.token) {
      notify("需要学生身份验证");
      return;
    }
    const result = await EnrollmentService.deleteSelection({
      studentToken: userInfo.token,
      termId: selectedTrace.term.id,
      selectionId: selectedEnrollment.selection.id,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("已取消选课", "success");
      setOpenDialog(false);
      setIsEditingStake(false);
      setIsEditingSelection(false);
      setIsAddingSelection(false);
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
            result.traces[0] ||
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

  useEffect(() => {
    if (!openDialog || !selectedEnrollment?.selection?.courseId) {
      setCommentList([]);
      return;
    }
    setIsCommentLoading(true);
    CurriculumService.queryComments({
      courseId: selectedEnrollment.selection.courseId,
    })
      .then((result) => {
        if (result instanceof Error) notify(result.message);
        else setCommentList(result);
      })
      .finally(() => setIsCommentLoading(false));
  }, [openDialog, selectedEnrollment?.selection?.courseId, notify]);

  const handleSendComment = async () => {
    if (!userInfo?.token) {
      notify("需要学生身份验证");
      return;
    }
    if (!selectedEnrollment?.selection?.courseId) {
      notify("未选中课程");
      return;
    }
    if (!commentInput.trim()) {
      notify("评论内容不能为空");
      return;
    }
    const result = await CurriculumService.createComment({
      studentToken: userInfo.token,
      courseId: selectedEnrollment.selection.courseId,
      content: commentInput.trim(),
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("评论已发送", "success");
      setCommentInput("");
      // 重新加载评论
      setIsCommentLoading(true);
      CurriculumService.queryComments({
        courseId: selectedEnrollment.selection.courseId,
      })
        .then((result) => {
          if (!(result instanceof Error)) setCommentList(result);
        })
        .finally(() => setIsCommentLoading(false));
    }
  };

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
        selectedEnrollments.length ? (
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
        ) : null
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
            {selectedEnrollment?.selection ? (
              <>
                <Slider
                  disabled={selectedTrace?.term.phase !== Phase.Enrolling}
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
                <Stack spacing={2}>
                  {isEditingStake && (
                    <Button variant="contained" onClick={handleEditStake}>
                      确认修改
                    </Button>
                  )}
                  <Button
                    disabled={selectedTrace?.term.phase !== Phase.Enrolling}
                    variant="text"
                    color="error"
                    onClick={handleDeleteSelection}
                  >
                    取消选课
                  </Button>
                </Stack>
              </>
            ) : null}

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
                      disabled={selectedTrace?.term.phase !== Phase.Enrolling}
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
            {selectedEnrollment?.selection?.courseId && (
              <Box>
                <Typography variant="subtitle1" gutterBottom>
                  课程评论
                </Typography>
                {isCommentLoading ? (
                  <Typography variant="body2" color="text.secondary">
                    加载中...
                  </Typography>
                ) : (
                  <Stack spacing={1} mb={1}>
                    {commentList.length ? (
                      commentList.map((comment) => (
                        <Box
                          key={comment.id}
                          sx={{ bgcolor: "grey.100", p: 1, borderRadius: 1 }}
                        >
                          <Typography variant="body2">
                            {comment.content}
                          </Typography>
                        </Box>
                      ))
                    ) : (
                      <Typography variant="body2" color="text.secondary">
                        暂无评论
                      </Typography>
                    )}
                  </Stack>
                )}
                <Stack direction="row" spacing={1} alignItems="center">
                  <input
                    type="text"
                    value={commentInput}
                    onChange={(e) => setCommentInput(e.target.value)}
                    placeholder="输入评论..."
                    style={{
                      flex: 1,
                      padding: 8,
                      borderRadius: 4,
                      border: "1px solid #ccc",
                    }}
                    onKeyDown={(e) => {
                      if (e.key === "Enter") handleSendComment();
                    }}
                  />
                  <Button
                    variant="contained"
                    size="small"
                    onClick={handleSendComment}
                  >
                    发送
                  </Button>
                </Stack>
              </Box>
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
