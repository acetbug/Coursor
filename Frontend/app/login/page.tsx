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
import { useState } from "react";
import UserService from "@/lib/services/userService";
import { useNotification } from "@/components/notificationProvider";

interface LoginForm {
  userId: string;
  password: string;
}

export default function LoginPage() {
  const { notify } = useNotification();

  const [form, setForm] = useState<LoginForm>({
    userId: "",
    password: "",
  });

  const [error, setError] = useState<LoginForm>({
    userId: "",
    password: "",
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
    setError({
      ...error,
      [e.target.name]: e.target.value === "" ? "Required" : "",
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (error.userId || error.password || !form.userId || !form.password) {
      notify("Please fill in all required fields correctly.");
      return;
    }
    try {
      await UserService.login({
        userId: form.userId,
        password: CryptoJS.SHA256(form.password).toString(),
      });
    } catch (error) {
      let errorMessage = "An error occurred during login";
      if (error instanceof Error) errorMessage = error.message;
      notify(errorMessage);
      return;
    }
  };

  return (
    <Box
      sx={{
        minHeight: "100vh",
        display: "flex",
        alignItems: "center",
        bgcolor: "background.default",
      }}
    >
      <Container maxWidth="sm">
        <Paper elevation={3} sx={{ p: 4, borderRadius: 2 }}>
          <Typography variant="h4" align="center" gutterBottom>
            Login
          </Typography>
          <Typography
            variant="body1"
            align="center"
            color="text.secondary"
            sx={{ mb: 3 }}
          >
            Sign in to access your course selection
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
              label="User ID"
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
              label="Password"
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
              Sign In
            </Button>
          </Box>
        </Paper>
      </Container>
    </Box>
  );
}
