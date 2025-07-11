"use client";

import { useAuth } from "@/context/authContext";
import { useNotification } from "@/context/notificationContext";
import UserService from "@/services/userService";
import { Role, User } from "@/types/userService";
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
  Typography,
} from "@mui/material";
import { useEffect, useState } from "react";
import {
  Department,
  Stage,
  StageMap,
  Student,
  Term,
} from "@/types/registerService";
import RegisterService from "@/services/registerService";

export default function AdminStudentsPage() {
  const { notify } = useNotification();
  const { userInfo } = useAuth();
  const [flag, setFlag] = useState(false);
  const toggleFlag = () => setFlag((prev) => !prev);
  const [departmentList, setDepartmentList] = useState<Department[]>([]);
  const [selectedDepartmentId, setSelectedDepartmentId] = useState("");
  const [termList, setTermList] = useState<Term[]>([]);
  const [selectedTermId, setSelectedTermId] = useState("");
  const [selectedStage, setSelectedStage] = useState<Stage | null>(null);
  const [studentList, setStudentList] = useState<User[]>([]);
  const [selectedStudent, setSelectedStudent] = useState<User | null>(null);
  const [studentInfo, setStudentInfo] = useState<Student | null>(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [isDeletingRegister, setIsDeletingRegister] = useState(false);
  const [isAddingRegister, setIsAddingRegister] = useState(false);
  const [isAddingTrace, setIsAddingTrace] = useState(false);
  const [isDeletingTrace, setIsDeletingTrace] = useState(false);

  const handleAddRegister = async () => {
    if (!selectedDepartmentId) {
      notify("Please select a department");
      return;
    }
    if (!selectedStudent) {
      notify("No student selected");
      return;
    }
    if (!userInfo?.token) {
      notify("Admin token is required");
      return;
    }
    const result = await RegisterService.createRegister({
      adminToken: userInfo.token,
      studentId: selectedStudent.id,
      departmentId: selectedDepartmentId,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("Register added successfully", "success");
      toggleFlag();
      setIsAddingRegister(false);
      setSelectedDepartmentId("");
    }
  };

  const handleDeleteRegister = async () => {
    if (!selectedStudent) {
      notify("No student selected");
      return;
    }
    if (!studentInfo?.department) {
      notify("No department registered for this student");
      return;
    }
    if (!userInfo?.token) {
      notify("Admin token is required");
      return;
    }
    const result = await RegisterService.deleteRegister({
      adminToken: userInfo.token,
      studentId: selectedStudent.id,
      departmentId: studentInfo.department.id,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("Register deleted successfully", "success");
      toggleFlag();
      setIsDeletingRegister(false);
    }
  };

  const handleAddTrace = async () => {
    if (!selectedTermId || !selectedStage) {
      notify("Please select term and stage");
      return;
    }
    if (!selectedStudent) {
      notify("No student selected");
      return;
    }
    if (!userInfo?.token) {
      notify("Admin token is required");
      return;
    }
    const result = await RegisterService.createTrace({
      adminToken: userInfo.token,
      studentId: selectedStudent.id,
      termId: selectedTermId,
      stage: selectedStage,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("Trace added successfully", "success");
      toggleFlag();
      setIsAddingTrace(false);
      setSelectedTermId("");
      setSelectedStage(null);
    }
  };

  const handleDeleteTrace = async () => {
    if (!selectedTermId) {
      notify("No term selected");
      return;
    }
    if (!selectedStudent) {
      notify("No student selected");
      return;
    }
    if (!userInfo?.token) {
      notify("Admin token is required");
      return;
    }
    const result = await RegisterService.deleteTrace({
      adminToken: userInfo.token,
      studentId: selectedStudent.id,
      termId: selectedTermId,
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("Trace deleted successfully", "success");
      toggleFlag();
      setIsDeletingTrace(false);
      setSelectedTermId("");
    }
  };
  useEffect(() => {
    UserService.queryUsers({ role: Role.Student }).then((result) => {
      if (result instanceof Error) notify(result.message);
      else setStudentList(result);
    });
    RegisterService.queryDepartments({ child: undefined }).then((result) => {
      if (result instanceof Error) notify(result.message);
      else setDepartmentList(result);
    });
    RegisterService.queryTerms({ child: undefined }).then((result) => {
      if (result instanceof Error) notify(result.message);
      else setTermList(result);
    });
  }, [flag, notify]);

  useEffect(() => {
    if (!selectedStudent) return;
    RegisterService.queryStudent({ studentId: selectedStudent.id }).then(
      (result) => {
        if (result instanceof Error) notify(result.message);
        else setStudentInfo(result);
      }
    );
  }, [flag, notify, selectedStudent]);

  return (
    <Stack display="flex" flex={1}>
      <Grid container maxWidth="lg" p={4} spacing={2} alignContent="center">
        {studentList.map((student) => (
          <Grid key={student.id} size="auto">
            <Card variant="outlined">
              <CardActionArea
                onClick={() => {
                  setSelectedStudent(student);
                  setOpenDialog(true);
                }}
              >
                <CardContent>
                  <Typography variant="h6" gutterBottom>
                    {student.name}
                  </Typography>
                </CardContent>
              </CardActionArea>
            </Card>
          </Grid>
        ))}
      </Grid>

      <Dialog open={openDialog} onClose={() => setOpenDialog(false)} fullWidth>
        <DialogTitle>{selectedStudent?.name} 的学籍信息</DialogTitle>

        <DialogContent>
          <Stack spacing={2} p={2}>
            <Stack spacing={1}>
              <Typography variant="body1" alignContent="center" color="primary">
                院系
              </Typography>

              <Box display="flex">
                {studentInfo?.department ? (
                  <>
                    <Typography
                      flex={1}
                      variant="body2"
                      mr={2}
                      alignContent={"center"}
                    >
                      {studentInfo.department.name}
                    </Typography>

                    {isDeletingRegister ? (
                      <Button
                        variant="text"
                        color="error"
                        onClick={handleDeleteRegister}
                      >
                        确定删除
                      </Button>
                    ) : (
                      <IconButton onClick={() => setIsDeletingRegister(true)}>
                        <Delete />
                      </IconButton>
                    )}
                  </>
                ) : isAddingRegister ? (
                  <>
                    <Select
                      required
                      label="选择院系"
                      value={selectedDepartmentId}
                      onChange={(e) => setSelectedDepartmentId(e.target.value)}
                      sx={{ flex: 1, mr: 2 }}
                    >
                      {departmentList.map((department) => (
                        <MenuItem key={department.id} value={department.id}>
                          {department.name}
                        </MenuItem>
                      ))}
                    </Select>

                    <Button variant="contained" onClick={handleAddRegister}>
                      添加
                    </Button>
                  </>
                ) : (
                  <>
                    <Typography
                      flex={1}
                      variant="body2"
                      color="textSecondary"
                      mr={2}
                      alignContent={"center"}
                    >
                      未设置
                    </Typography>

                    <IconButton onClick={() => setIsAddingRegister(true)}>
                      <Add />
                    </IconButton>
                  </>
                )}
              </Box>
            </Stack>

            <Stack spacing={1}>
              <Box display="flex">
                <Typography
                  variant="body1"
                  flex={1}
                  alignContent="center"
                  color="primary"
                >
                  学期记录
                </Typography>

                <IconButton
                  onClick={() => {
                    setIsDeletingTrace(false);
                    setSelectedTermId("");
                    setIsAddingTrace(true);
                  }}
                >
                  <Add />
                </IconButton>
              </Box>

              {studentInfo?.traces.length
                ? studentInfo.traces.map((trace) => (
                    <Box key={trace.term.id} display="flex" alignItems="center">
                      <Typography flex={1} variant="body2">
                        {trace.term.name} - {StageMap[trace.stage]}
                      </Typography>

                      {isDeletingTrace && selectedTermId === trace.term.id ? (
                        <Button
                          variant="text"
                          color="error"
                          onClick={handleDeleteTrace}
                        >
                          确定删除
                        </Button>
                      ) : (
                        <IconButton
                          onClick={() => {
                            setIsAddingTrace(false);
                            setSelectedTermId(trace.term.id);
                            setIsDeletingTrace(true);
                          }}
                        >
                          <Delete />
                        </IconButton>
                      )}
                    </Box>
                  ))
                : !isAddingTrace && (
                    <Typography variant="body2" color="textSecondary">
                      无学期记录
                    </Typography>
                  )}

              {isAddingTrace && (
                <Box display="flex" alignItems="center">
                  <Select
                    required
                    label="选择学期"
                    value={selectedTermId}
                    onChange={(e) => setSelectedTermId(e.target.value)}
                    sx={{ flex: 1, mr: 2 }}
                  >
                    {termList
                      .filter(
                        (term) =>
                          !studentInfo?.traces.some(
                            (trace) => trace.term.id === term.id
                          )
                      )
                      .map((term) => (
                        <MenuItem key={term.id} value={term.id}>
                          {term.name}
                        </MenuItem>
                      ))}
                  </Select>

                  <Select
                    required
                    label="选择阶段"
                    value={selectedStage || ""}
                    onChange={(e) => setSelectedStage(e.target.value as Stage)}
                    sx={{ flex: 1, mr: 2 }}
                  >
                    {Object.values(Stage).map((stage) => (
                      <MenuItem key={stage} value={stage}>
                        {StageMap[stage]}
                      </MenuItem>
                    ))}
                  </Select>

                  <Button variant="contained" onClick={handleAddTrace}>
                    添加记录
                  </Button>
                </Box>
              )}
            </Stack>
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
