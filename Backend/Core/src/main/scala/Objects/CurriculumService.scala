package Objects

case class Curriculum(
    stage: Stage,
    recommendations: List[Recommendation]
)

case class Recommendation(
    id: String,
    subject: Subject,
    priority: Int
)

case class Subject(
    id: String,
    name: String,
    credits: Int
)

case class Teaching(
    subject: Subject,
    lectures: List[Lecture]
)

case class Lecture(
    id: String,
    term: Term,
    location: String,
    schedule: String,
    capacity: Int
)

case class Course(
    id: String,
    teacher: User,
    location: String,
    schedule: String,
    capacity: Int
)

case class Comment(
    id: String,
    content: String
)

case class Review(
    term: Term,
    comments: List[Comment]
)
