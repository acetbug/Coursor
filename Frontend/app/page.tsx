import {
  AppBar,
  Box,
  Button,
  Card,
  CardContent,
  Container,
  Grid,
  Toolbar,
  Typography,
} from "@mui/material";

export default function Home() {
  return (
    <>
      <AppBar position="static" color="primary">
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            Coursor
          </Typography>
          <Button color="inherit" href="/login">
            登录
          </Button>
        </Toolbar>
      </AppBar>

      <Box sx={{ bgcolor: "background.default", py: 8 }}>
        <Container maxWidth="lg">
          <Typography
            variant="h3"
            align="center"
            color="text.primary"
            gutterBottom
          >
            欢迎来到 Coursor
          </Typography>
          <Typography variant="h5" align="center" color="text.secondary">
            选择数百门课程，构建您完美的学习路径
          </Typography>

          <Grid container spacing={4} sx={{ mt: 4 }}>
            {[1, 2, 3].map((item) => (
              <Card
                key={item}
                sx={{
                  height: "100%",
                  transition: "transform 0.3s",
                  "&:hover": { transform: "translateY(-8px)" },
                }}
              >
                <CardContent sx={{ p: 3 }}>
                  <Typography variant="h5" gutterBottom>
                    Feature {item}
                  </Typography>
                  <Typography color="text.secondary">
                    Description of feature {item} that highlights the benefits
                    of our course selection system.
                  </Typography>
                </CardContent>
              </Card>
            ))}
          </Grid>

          <Box sx={{ mt: 8, textAlign: "center" }}>
            <Button variant="contained" size="large" href="/login">
              从这里开始
            </Button>
          </Box>
        </Container>
      </Box>

      <Box component="footer" sx={{ py: 6, bgcolor: "background.paper" }}>
        <Container maxWidth="lg">
          <Typography variant="body2" color="text.secondary" align="center">
            © {new Date().getFullYear()} Coursor. All rights reserved.
          </Typography>
        </Container>
      </Box>
    </>
  );
}
