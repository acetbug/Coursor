import { Stage, Term } from "./registerService";
import { User } from "./userService";

export interface Curriculum {
  stage: Stage;
  recommendations: Recommendation[];
}

export interface Recommendation {
  id: string;
  subject: Subject;
  priority: number;
}

export interface Subject {
  id: string;
  name: string;
  credits: number;
}

export interface Teaching {
  subject: Subject;
  lectures: Lecture[];
}

export interface Lecture {
  id: string;
  term: Term;
  location: string;
  schedule: string;
  capacity: number;
}

export interface Course {
  id: string;
  teacher: User;
  location: string;
  schedule: string;
  capacity: number;
}

export interface Comment {
  id: string;
  content: string;
}

export interface Review {
  term: Term;
  comments: Comment[];
}
