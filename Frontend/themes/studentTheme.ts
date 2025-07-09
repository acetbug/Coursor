import { createTheme } from "@mui/material";
import { teal } from "@mui/material/colors";

const studentTheme = createTheme({
  palette: {
    primary: {
      main: teal[500],
    },
  },
});

export default studentTheme;
