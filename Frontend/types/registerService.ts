export interface Student {
  department?: Department;
  traces: Trace[];
}

export interface Department {
  id: string;
  name: string;
}

export interface Term {
  id: string;
  name: string;
  phase: Phase;
}

export interface Trace {
  term: Term;
  stage: Stage;
}

export enum Phase {
  Enrolling = "Enrolling",
  Confirmed = "Confirmed",
}

export enum Stage {
  Freshman_0 = "Freshman_0",
  Freshman_1 = "Freshman_1",
  Freshman_2 = "Freshman_2",
  Sophomore_0 = "Sophomore_0",
  Sophomore_1 = "Sophomore_1",
  Sophomore_2 = "Sophomore_2",
  Junior_0 = "Junior_0",
  Junior_1 = "Junior_1",
  Junior_2 = "Junior_2",
  Senior_0 = "Senior_0",
  Senior_1 = "Senior_1",
  Senior_2 = "Senior_2",
}

export const StageMap: Record<Stage, string> = {
  Freshman_0: "大一小学期",
  Freshman_1: "大一上学期",
  Freshman_2: "大一下学期",
  Sophomore_0: "大二小学期",
  Sophomore_1: "大二上学期",
  Sophomore_2: "大二下学期",
  Junior_0: "大三小学期",
  Junior_1: "大三上学期",
  Junior_2: "大三下学期",
  Senior_0: "大四小学期",
  Senior_1: "大四上学期",
  Senior_2: "大四下学期",
};
