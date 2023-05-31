package com.singhraghav.catspractice.myserver

import cats._
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe._
import cats.effect.{IO, IOApp}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.{OptionalValidatingQueryParamDecoderMatcher, QueryParamDecoderMatcher}
import org.http4s.ember.server.EmberServerBuilder

import java.util.UUID

object Http4s extends IOApp.Simple {

  type Student = String

  case class Instructor(firstName: String, lastName: String)

  case class Course(id: String, title: String, year: Int, students: List[Student], instructorName: String)

  object CourseRepository {
    private val catsEffectCourse = Course("8e3142b3-3b7c-4a34-a272-e35aaf5e5516", "Cats Effect 3", 2023, List("Raghav", "Anshu"), "Daniel")
    private val courses: Map[String, Course] = Map(catsEffectCourse.id -> catsEffectCourse)

    def findCoursesById(courseId: UUID): Option[Course] = courses.get(courseId.toString)

    def findCoursesByInstructor(instructorName: String): List[Course] = courses.values.filter(_.instructorName.equals(instructorName)).toList
  }

  object InstructorQueryParamMatcher extends QueryParamDecoderMatcher[String]("instructor")

  object YearQueryParamMatcher extends OptionalValidatingQueryParamDecoderMatcher[Int]("year")

  def courseRoutes[F[_]: Monad]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._

    HttpRoutes.of[F] {
      case GET -> Root / "courses" :? InstructorQueryParamMatcher(instructor)  +& YearQueryParamMatcher(maybeYear) =>
        val courses = CourseRepository.findCoursesByInstructor(instructor)
        maybeYear match {
          case Some(y) => y.fold(
            _ => BadRequest("Parameter year is invalid"),
            year => Ok(courses.filter(_.year == year).asJson)
          )
          case None => Ok(courses.asJson)
        }

      case GET -> Root / "courses" / UUIDVar(courseId) / "students" =>
        CourseRepository.findCoursesById(courseId).map(_.students) match {
          case Some(students) => Ok(students.asJson)
          case None => NotFound(s"No Course with id $courseId was found")
        }
    }
  }
  override def run: IO[Unit] = EmberServerBuilder
    .default[IO]
    .withHttpApp(courseRoutes[IO].orNotFound)
    .build
    .use(_ => IO.println("Server ready!") *> IO.never)
}
