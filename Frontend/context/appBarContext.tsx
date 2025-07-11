"use client";

import {
  AppBar,
  Box,
  Button,
  Theme,
  ThemeProvider,
  Toolbar,
  Typography,
} from "@mui/material";
import { createContext, ReactNode, useContext, useState } from "react";
import defaultTheme from "@/themes/defaultTheme";
import { useAuth } from "./authContext";
import { useRouter } from "next/navigation";

interface Tab {
  label: string;
  href: string;
}

interface AppBarProps {
  theme: Theme;
  tabs: Tab[];
}

interface AppBarContextType {
  setAppBar: (props: AppBarProps) => void;
}

const AppBarContext = createContext<AppBarContextType | undefined>(undefined);

export function AppBarProvider({ children }: { children: ReactNode }) {
  const router = useRouter();
  const { userInfo, logout } = useAuth();
  const [appBar, setAppBar] = useState<AppBarProps>({
    theme: defaultTheme,
    tabs: [],
  });

  return (
    <ThemeProvider theme={appBar.theme}>
      <AppBarContext.Provider value={{ setAppBar }}>
        <AppBar position="sticky" color="primary">
          <Toolbar>
            <Typography mr={2} variant="h6" onClick={() => router.push("/")} sx={{ cursor: "pointer" }}>
              Coursor
            </Typography>
            {appBar.tabs.map((tab) => (
              <Button
                variant="text"
                color="inherit"
                key={tab.label}
                onClick={() => router.push(tab.href)}
              >
                <Typography variant="body1">{tab.label}</Typography>
              </Button>
            ))}

            <Box sx={{ flexGrow: 1 }} />

            {userInfo ? (
              <>
                <Typography sx={{ ml: 2, mr: 2 }}>
                  欢迎, {userInfo.name}
                </Typography>
                <Button
                  variant="text"
                  color="inherit"
                  onClick={() => {
                    logout();
                    setAppBar({ theme: defaultTheme, tabs: [] });
                  }}
                >
                  登出
                </Button>
              </>
            ) : (
              <></>
            )}
          </Toolbar>
        </AppBar>
        {children}
      </AppBarContext.Provider>
    </ThemeProvider>
  );
}

export function useAppBar() {
  const context = useContext(AppBarContext);
  if (!context) {
    throw new Error("useAppBar must be used within an AppBarProvider");
  }
  return context;
}
