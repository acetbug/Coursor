package Global

object UserService extends Service(10010, "user_service"):
  val userTable = "user_table"
  val authTable = "auth_table"

object RegisterService extends Service(10011, "register_service"):
  val departmentTable = "department_table"
  val termTable = "term_table"
  val registerTable = "register_table"
  val traceTable = "trace_table"

object CurriculumService extends Service(10012, "curriculum_service"):
  val subjectTable = "subject_table"
  val recommendationTable = "recommendation_table"
  val courseTable = "course_table"
  val commentTable = "comment_table"

object EnrollmentService extends Service(10013, "enrollment_service"):
  val enrollmentTable = "enrollment_table"
