"use client";

import React, {
  createContext,
  ReactNode,
  useCallback,
  useContext,
  useState,
} from "react";
import { Alert, Slide, Snackbar } from "@mui/material";

type NotificationSeverity = "success" | "info" | "warning" | "error";

interface NotificationMessage {
  message: string;
  severity: NotificationSeverity;
}

type NotificationContextType = {
  notify: (message: string, severity?: NotificationSeverity) => void;
};

const NotificationContext = createContext<NotificationContextType | undefined>(
  undefined
);

export const useNotification = () => {
  const context = useContext(NotificationContext);
  if (!context) {
    throw new Error(
      "useNotification must be used within a NotificationProvider"
    );
  }
  return context;
};

export const NotificationProvider: React.FC<{ children: ReactNode }> = ({
  children,
}) => {
  const [open, setOpen] = useState(false);
  const [message, setMessage] = useState<NotificationMessage | null>(null);

  const showNotification = useCallback(
    (message: string, severity: NotificationSeverity = "error") => {
      setMessage({ message, severity });
      setOpen(true);
    },
    []
  );

  return (
    <NotificationContext.Provider value={{ notify: showNotification }}>
      {children}
      <Snackbar
        open={open}
        autoHideDuration={3000}
        onClose={(_, reason) => {
          if (reason === "clickaway") {
            return;
          }
          setOpen(false);
        }}
        anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
        slots={{ transition: Slide }}
      >
        <Alert
          onClose={() => {
            setOpen(false);
          }}
          severity={message?.severity}
        >
          {message?.message}
        </Alert>
      </Snackbar>
    </NotificationContext.Provider>
  );
};
