import { Stage } from "@/types/registerService";
import ApiClient from "./apiClient";
import {
  Course,
  Curriculum,
  Recommendation,
  Review,
  Subject,
  Teaching,
} from "@/types/curriculumService";

const port = 10012;
const client = new ApiClient(port);

const CurriculumService = {
  createSubject: client.api<
    { adminToken: string; name: string; credits: number },
    void
  >("CreateSubjectMessage"),

  createRecommendation: client.api<
    {
      adminToken: string;
      departmentId: string;
      subjectId: string;
      stage: Stage;
      priority: number;
    },
    void
  >("CreateRecommendationMessage"),

  createCourse: client.api<
    {
      teacherToken: string;
      subjectId: string;
      termId: string;
      location: string;
      schedule: string;
      capacity: number;
    },
    void
  >("CreateCourseMessage"),

  createComment: client.api<
    {
      studentToken: string;
      courseId: string;
      content: string;
    },
    void
  >("CreateCommentMessage"),

  querySubjects: client.api<{ child: void }, Subject[]>("QuerySubjectsMessage"),

  queryCurricula: client.api<{ departmentId: string }, Curriculum[]>(
    "QueryCurriculaMessage"
  ),

  queryRecommendations: client.api<
    { departmentId: string; stage: Stage },
    Recommendation[]
  >("QueryRecommendationsMessage"),

  queryTeachings: client.api<{ teacherId: string; termId: string }, Teaching[]>(
    "QueryTeachingsMessage"
  ),

  queryCourses: client.api<{ subjectId: string; termId: string }, Course[]>(
    "QueryCoursesMessage"
  ),

  queryComments: client.api<{ courseId: string }, Comment[]>(
    "QueryCommentsMessage"
  ),

  queryReviews: client.api<{ subjectId: string; teacherId: string }, Review[]>(
    "QueryReviewsMessage"
  ),

  updateSubject: client.api<
    { adminToken: string; subjectId: string; name: string; credits: number },
    void
  >("UpdateSubjectMessage"),

  updateCourse: client.api<
    {
      teacherToken: string;
      courseId: string;
      location: string;
      schedule: string;
      capacity: number;
    },
    void
  >("UpdateCourseMessage"),

  deleteSubject: client.api<{ adminToken: string; subjectId: string }, void>(
    "DeleteSubjectMessage"
  ),

  deleteRecommendation: client.api<
    { adminToken: string; recommendationId: string },
    void
  >("DeleteRecommendationMessage"),

  deleteCourse: client.api<{ teacherToken: string; courseId: string }, void>(
    "DeleteCourseMessage"
  ),

  deleteComment: client.api<{ studentToken: string; commentId: string }, void>(
    "DeleteCommentMessage"
  ),
};

export default CurriculumService;
