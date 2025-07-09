import { Container, Stack, Typography } from "@mui/material";

export default function AdminPage() {
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
          欢迎来到 Coursor 管理系统
        </Typography>

        <Typography variant="h5" color="text.secondary">
          请从顶栏选择您需要的功能
        </Typography>
      </Stack>
    </Container>
  );
}
