import { createTheme } from "@mui/material";
import { deepPurple } from "@mui/material/colors";

const defaultTheme = createTheme({
  palette: {
    primary: {
      main: deepPurple[500],
    },
  },
});

export default defaultTheme;
