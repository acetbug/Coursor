import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import { AppRouterCacheProvider } from "@mui/material-nextjs/v13-appRouter";
import { NotificationProvider } from "@/context/notificationContext";
import { ReactNode } from "react";
import { AuthProvider } from "@/context/authContext";
import { AppBarProvider } from "@/context/appBarContext";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "Coursor",
  description: "A platform for course selection and management",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: ReactNode;
}>) {
  return (
    <html lang="zh">
      <body className={`${geistSans.variable} ${geistMono.variable}`}>
        <AppRouterCacheProvider>
          <NotificationProvider>
            <AuthProvider>
              <AppBarProvider>{children}</AppBarProvider>
            </AuthProvider>
          </NotificationProvider>
        </AppRouterCacheProvider>
      </body>
    </html>
  );
}
