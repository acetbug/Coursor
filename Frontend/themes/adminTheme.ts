import { createTheme } from "@mui/material";
import { indigo } from "@mui/material/colors";

const adminTheme = createTheme({
  palette: {
    primary: {
      main: indigo[500],
    },
  },
});

export default adminTheme;
