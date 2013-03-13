package training


import service.EnrollmentService
import util.JPAUtil
import domain.{Student, Course,CourseTitle,BookingException}
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

import org.specs2.mutable._
import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{StaticQuery => Q}
import Q.interpolation
import org.specs2.specification.Scope
import org.joda.time.LocalDate
import nl.jqno.equalsverifier.{Warning, EqualsVerifier}
import java.sql.{Date, SQLData}

/**
 * todo
 */

@RunWith(classOf[JUnitRunner])
class CourseBookingIntegrationTest extends Specification {
  sequential

  "a student" should {

    "register and have 10 tokens" in new company {
      val id = service.registerStudent("jennifer", "secret","5555-3333-2222-1111", LocalDate.now().plusYears(3),"jenny@work.com")

      db withSession {
        val tokens = sql"select tokens from Student where id=$id".as[Long].first()
        tokens must beEqualTo(10)
      }

    }

    "register with a password" in new company {
      val id = service.registerStudent("jennifer", "secret","5555-3333-2222-1111", LocalDate.now().plusYears(3),"jenny@work.com")

      db withSession {
        val password = sql"select password from Password where id=$id".as[String].first()
        password must beEqualTo("secret")
      }
    }

    "register with an email" in new company {
      val id = service.registerStudent("jennifer", "secret","5555-3333-2222-1111", LocalDate.now().plusYears(3),"jenny@work.com")

      db withSession {
        val firstEmail = sql"select email from Student_emailAddresses where Student_id=$id and idx=0".as[String].first()
        firstEmail must beEqualTo("jenny@work.com")
      }
    }


    "register with a creditcard" in new company {
      val id = service.registerStudent("jennifer", "secret","5555-3333-2222-1111", LocalDate.now().plusYears(3),"jenny@work.com")

      db withSession {
        val cardNumber = sql"select cardNumber from Student where id=$id".as[String].first()
        cardNumber must beEqualTo("5555-3333-2222-1111")
      }

    }

    "book for a course and pay tokens" in new company {
      service.registerStudentForEvent(studentId, courseId)


      db withSession {
        val tokens = sql"select tokens from Student where id=$studentId".as[Long].first()
        tokens must beEqualTo(7)
      }
    }

    "not be able to book for a course which starts in less than two days" in new company {
      service.registerStudentForEvent(studentId, courseInTwoDaysId) must throwA[BookingException]

    }

    "not be able to book for a course on which he/she is already registerd" in new company {
      service.registerStudentForEvent(studentId, courseId)

      service.registerStudentForEvent(studentId, courseId) must throwA[BookingException]

    }
    "not be able to book on a full course" in new company {
      service.registerStudentForEvent(studentId, fullCourseId) must throwA[BookingException]

    }

    "not be able to book when insufficient tokens" in new company {
      service.registerStudentForEvent(studentWithOneTokenId, courseId) must throwA[BookingException]

    }

    "not book for event when card expires within a month" in new company {
      service.registerStudentForEvent(studentWithExpiringCard, courseId) must throwA[BookingException]

    }


  }

  "equals contract" should {
    "be correct for Student" in {
      EqualsVerifier.forClass(classOf[Student]).suppress(Warning.NULL_FIELDS, Warning.STRICT_INHERITANCE).verify()
    }

    "be correct for Course" in {
      EqualsVerifier.forClass(classOf[Course]).suppress(Warning.NULL_FIELDS, Warning.STRICT_INHERITANCE).verify()
    }

    "be correct for CourseTitle" in {
      EqualsVerifier.forClass(classOf[CourseTitle]).suppress(Warning.NULL_FIELDS, Warning.STRICT_INHERITANCE).verify()
    }
  }

  trait company extends Scope {


    JPAUtil.init()
    // make service available
    val service = EnrollmentService.getInstance()

    // make sure tables are
    val db = Database.forURL("jdbc:mysql://localhost/jpademos", driver = "com.mysql.jdbc.Driver", user = "root", password = "masterkey")

    // ids to use in the test
    val studentId = 1l
    val studentWithOneTokenId = 2l
    val studentWithExpiringCard = 3l
    val studentWithGoodCard = 4l


    val jb297Id: Int = 1
    val courseId = 1
    val dateFourWeeksFromNow = new Date(LocalDate.now().plusWeeks(4).toDate.getTime)

    val courseInTwoDaysId = 2
    val dateInTwoDaysFromNow = new Date(LocalDate.now().plusDays(2).toDate.getTime)
    val fullCourseId = 4
    val expDateInLessThanMonth = new java.sql.Date(LocalDate.now().plusMonths(1).minusDays(1).toDate.getTime)
    val expDateInOneMonth = new java.sql.Date(LocalDate.now().plusMonths(1).plusDays(1).toDate.getTime)
    // Seed the database
    db withSession {


      sqlu"delete from Student_Course".execute
      sqlu"delete from Course".execute
      sqlu"delete from CourseTitle".execute
      sqlu"delete from Password".execute
      sqlu"delete from Student_emailAddresses".execute
      sqlu"delete from Student".execute

      // Add a new course title
      sqlu"insert into CourseTitle (id,courseCode,durationInDays,title) values ($jb297Id,'JB297',3,'JPA')".execute

      //Plan three courses
      sqlu"insert into Course (id,courseTitle_id,startDate) values ($courseId,$jb297Id,$dateFourWeeksFromNow)".execute
      sqlu"insert into Course (id,courseTitle_id,startDate) values ($courseInTwoDaysId,$jb297Id,$dateInTwoDaysFromNow)".execute
      sqlu"insert into Course (id,courseTitle_id,startDate) values ($fullCourseId,$jb297Id,$dateFourWeeksFromNow)".execute

      for (i <- 1000 to 1010) {
        val name ="janne"+i
        sqlu"insert into Student (id,username,tokens) values ($i,$name,9)".execute
        sqlu"insert into Student_Course (students_id,registeredEvents_id) values ($i,$fullCourseId)".execute
      }

      // Insert students
      sqlu"insert into Student (id,username,tokens,cardNumber,expDate) values ($studentId,'cindy',10,'5555-4444-3333-2222',$expDateInOneMonth)".execute
      sqlu"insert into Student (id,username,tokens,cardNumber,expDate) values ($studentWithOneTokenId,'narendra',1,'5555-4444-3333-2222',$expDateInOneMonth)".execute

      sqlu"insert into Student (id,username,tokens,cardNumber,expDate) values ($studentWithExpiringCard,'john',10,'5555-4444-3333-2222',$expDateInLessThanMonth)".execute

      sqlu"insert into Student (id,username,tokens,cardNumber,expDate) values ($studentWithGoodCard,'ahmet',10,'5555-4444-3333-2222',$expDateInOneMonth)".execute



    }
  }


}
