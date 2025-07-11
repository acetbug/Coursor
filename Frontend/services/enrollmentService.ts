import { Selection } from "@/types/enrollmentService";
import ApiClient from "./apiClient";

const port = 10013;
const client = new ApiClient(port);

const EnrollmentService = {
  createSelection: client.api<
    {
      studentToken: string;
      termId: string;
      subjectId: string;
      courseId: string;
    },
    void
  >("CreateSelectionMessage"),

  queryAllocatedStakePoints: client.api<
    { studentId: string; termId: string },
    number
  >("QueryAllocatedStakePointsMessage"),

  querySelections: client.api<
    { studentId: string; termId: string },
    Selection[]
  >("QuerySelectionsMessage"),

  updateStake: client.api<
    {
      studentToken: string;
      termId: string;
      selectionId: string;
      points: number;
    },
    void
  >("UpdateStakeMessage"),

  updateSelectedCourse: client.api<
    {
      studentToken: string;
      termId: string;
      selectionId: string;
      courseId: string;
    },
    void
  >("UpdateSelectedCourseMessage"),

  deleteSelection: client.api<
    { studentToken: string; termId: string; selectionId: string },
    void
  >("DeleteSelectionMessage"),

  executeEnrollment: client.api<{ adminToken: string; termId: string }, void>(
    "ExecuteEnrollmentMessage"
  ),
};

export default EnrollmentService;
