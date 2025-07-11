import { Subject } from "./curriculumService";

export interface Selection {
  id: string;
  subjectId: string;
  courseId: string;
  points: number;
}

export interface Enrollment {
  subject: Subject;
  selection?: Selection;
}
