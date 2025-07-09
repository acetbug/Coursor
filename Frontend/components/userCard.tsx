import { User } from "@/types/userService";
import { Card, Typography } from "@mui/material";

export default function UserCard({
  user,
  onClick,
}: {
  user: User;
  onClick: (user: User) => void;
}) {
  return (
    <Card
      variant="outlined"
      sx={{ p: 2, m: 2, cursor: "pointer" }}
      onClick={() => onClick(user)}
    >
      <Typography variant="h6" color="primary.dark">
        {user.name}
      </Typography>
    </Card>
  );
}
