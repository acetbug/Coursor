"use client";

import {
  Box,
  Container,
  Paper,
  Typography,
  TextField,
  Button,
} from "@mui/material";
import CryptoJS from "crypto-js";
import { useCallback, useEffect, useState } from "react";
import { useNotification } from "@/context/notificationContext";
import { useAuth } from "@/context/authContext";
import { useAppBar } from "@/context/appBarContext";
import defaultTheme from "@/themes/defaultTheme";
import UserService from "@/services/userService";

interface LoginForm {
  userId: string;
  password: string;
}

export default function LoginPage() {
  const { notify } = useNotification();
  const { userInfo, login, jumpDefault } = useAuth();
  const { setAppBar } = useAppBar();

  const [form, setForm] = useState<LoginForm>({
    userId: "",
    password: "",
  });

  const [error, setError] = useState<LoginForm>({
    userId: "",
    password: "",
  });

  const handleChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    setForm((prevForm) => ({
      ...prevForm,
      [e.target.name]: e.target.value,
    }));
    setError((prevError) => ({
      ...prevError,
      [e.target.name]: e.target.value === "" ? "Required" : "",
    }));
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (error.userId || error.password || !form.userId || !form.password) {
      notify("Please fill in all required fields correctly");
      return;
    }
    const result = await UserService.login({
      userId: form.userId,
      password: CryptoJS.SHA256(form.password).toString(),
    });
    if (result instanceof Error) notify(result.message);
    else {
      notify("Login successfully", "success");
      login({ id: form.userId, ...result });
    }
  };

  useEffect(() => {
    if (userInfo) jumpDefault(userInfo);
    setAppBar({
      theme: defaultTheme,
      tabs: [],
    });
  }, [jumpDefault, setAppBar, userInfo]);

  return (
    <Box sx={{ display: "flex", alignItems: "center", flex: 1 }}>
      <Container maxWidth="sm">
        <Paper elevation={3} sx={{ p: 4, borderRadius: 2 }}>
          <Typography
            variant="h4"
            color="primary.dark"
            align="center"
            gutterBottom
          >
            登录
          </Typography>
          <Typography
            variant="body1"
            align="center"
            color="text.secondary"
            sx={{ mb: 3 }}
          >
            登录以访问 Coursor
          </Typography>

          <Box
            component="form"
            onSubmit={handleSubmit}
            noValidate
            sx={{ mt: 1 }}
          >
            <TextField
              margin="normal"
              required
              fullWidth
              id="userid"
              label="用户名"
              name="userId"
              autoComplete="username"
              autoFocus
              value={form.userId}
              onChange={handleChange}
              error={!!error.userId}
              helperText={error.userId}
              sx={{ mb: 2 }}
            />
            <TextField
              margin="normal"
              required
              fullWidth
              name="password"
              label="密码"
              type="password"
              id="password"
              autoComplete="current-password"
              value={form.password}
              onChange={handleChange}
              error={!!error.password}
              helperText={error.password}
              sx={{ mb: 2 }}
            />
            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 2, py: 1.5 }}
            >
              登录
            </Button>
          </Box>
        </Paper>
      </Container>
    </Box>
  );
}
