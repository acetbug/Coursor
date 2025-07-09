import { User } from "@/types/userService";
import { Typography, Box, Grid, Container } from "@mui/material";
import UserCard from "./userCard";

export default function UserTable({
  userList,
  onChange,
}: {
  userList: User[];
  onChange: () => void;
}) {
  return userList.length > 0 ? (
    <Container sx={{ display: "flex", flex: 1, p: 2, maxWidth: "lg" }}>
      <Grid
        container
        width="100%"
        justifyContent="center"
        spacing={{ xs: 2, md: 3 }}
        columns={{ xs: 4, sm: 8, md: 12 }}
      >
        {userList.map((user, index) => (
          <Grid key={index} size={{ xs: 2, sm: 3, md: 4 }}>
            <UserCard
              user={user}
              onClick={() => {
                onChange();
              }}
            />
          </Grid>
        ))}
      </Grid>
    </Container>
  ) : (
    <Box sx={{ p: 3, textAlign: "center" }}>
      <Typography color="text.secondary">暂无用户数据</Typography>
    </Box>
  );
}
