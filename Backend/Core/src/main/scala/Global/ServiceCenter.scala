package Global

object AuthService extends Service(10010, "auth_service"):
  val authTable = "auth_table"

object CommentService extends Service(10011, "comment_service"):
  val commentTable = "comment_table"

object CourseService extends Service(10012, "course_service"):
  val courseTable = "course_table"

object DepartmentService extends Service(10013, "department_service"):
  val departmentTable = "department_table"
  val departmentSubjectRecommendationTable = "department_subject_recommendation_table"

object RegisterService extends Service(10014, "register_service"):
  val registerTable = "register_table"

object SelectionService extends Service(10015, "selection_service"):
  val selectionRecordTable = "selection_record_table"

object StudyService extends Service(10016, "study_service"):
  val studyTraceTable = "study_trace_table"
  val studyRecordTable = "study_record_table"

object SubjectService extends Service(10017, "subject_service"):
  val subjectTable = "subject_table"

object TermService extends Service(10018, "term_service"):
  val termTable = "term_table"

object UserService extends Service(10019, "user_service"):
  val userTable = "user_table"
