package training


import service.EnrollmentService
import util.JPAUtil
import domain.BookingException
import org.junit.runner.RunWith
import org.specs2.runner.JUnitRunner

import org.specs2.mutable._
import scala.slick.session.Database
import Database.threadLocalSession
import scala.slick.jdbc.{StaticQuery => Q}
import Q.interpolation
import org.specs2.specification.Scope

/**
 * todo
 */

@RunWith(classOf[JUnitRunner])
class CourseBookingIntegrationTest extends Specification {
  sequential

  "a student" should {

    "register" in new company {
      val id = service.registerStudent("jennifer")

      db withSession {
        val tokens = sql"select tokens from Student where id=$id".as[Long].first()
        tokens must beEqualTo(10)
      }

    }

    "book for a course and pay tokens" in new company {
      service.registerStudentForEvent(studentId, "JB297")

      db withSession {
        val tokens = sql"select tokens from Student where id=$studentId".as[Long].first()
        tokens must beEqualTo(9)
      }

    }


    "not book when insufficient tokens" in new company {
      service.registerStudentForEvent(studentWithZeroTokenId, "JB297") must throwA[BookingException]

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
    val studentWithZeroTokenId = 2l

    // Seed the database
    db withSession {

      val noOfRow: Int = sqlu"delete from Student".first

      sqlu"insert into Student (id,username,tokens) values ($studentId,'cindy',10)".execute
      sqlu"insert into Student (id,username,tokens) values ($studentWithZeroTokenId,'narendra',0)".execute



    }
  }


}
