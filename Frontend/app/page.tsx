"use client";

import { useAppBar } from "@/context/appBarContext";
import { useAuth } from "@/context/authContext";
import defaultTheme from "@/themes/defaultTheme";
import { Button, Container, Stack, Typography } from "@mui/material";
import { useEffect } from "react";

export default function HomePage() {
  const { jumpDefault } = useAuth();
  const { setAppBar } = useAppBar();

  useEffect(() => {
    setAppBar({
      theme: defaultTheme,
      tabs: [],
    });
  }, [setAppBar]);

  return (
    <Container sx={{ display: "flex", flex: 1 }}>
      <Stack
        flex={1}
        maxWidth="lg"
        spacing={4}
        alignItems="center"
        justifyContent="center"
      >
        <Typography
          variant="h2"
          component="h1"
          color="primary.dark"
          align="center"
          fontWeight={500}
          m={2}
          gutterBottom
        >
          欢迎来到 Coursor
        </Typography>

        <Typography variant="h5" color="text.secondary">
          不只是选课系统
        </Typography>

        <Button
          variant="contained"
          color="primary"
          size="large"
          onClick={() => jumpDefault()}
        >
          从这里开始
        </Button>
      </Stack>
    </Container>
  );
}
