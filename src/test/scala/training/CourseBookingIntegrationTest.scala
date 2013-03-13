package training


import service.EnrollmentService
import util.JPAUtil
import domain.{Student, BookingException}
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
import java.sql.SQLData

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
      service.registerStudentForEvent(studentId, "JB297")

      db withSession {
        val tokens = sql"select tokens from Student where id=$studentId".as[Long].first()
        tokens must beEqualTo(9)
      }
    }

    "not book when insufficient tokens" in new company {
      service.registerStudentForEvent(studentWithZeroTokenId, "JB297") must throwA[BookingException]

    }

    "book for event when insufficient card expires later than one month" in new company {
      service.registerStudentForEvent(studentWithGoodCard, "JB297")
      db withSession {
        val title = sql"select title from Student where id=$studentWithGoodCard".as[String].first()
        title must beEqualTo("JB297")
      }
    }

    "not book for event when insufficient card expires within a month" in new company {
      service.registerStudentForEvent(studentWithExpiringCard, "JB297") must throwA[BookingException]

    }


  }

  "equals contract" should {
    "be correct for Student" in {
      EqualsVerifier.forClass(classOf[Student]).suppress(Warning.NULL_FIELDS, Warning.STRICT_INHERITANCE).verify()
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
    val studentWithExpiringCard = 3l
    val studentWithGoodCard = 4l

    // Seed the database
    db withSession {

      sqlu"delete from Password".execute
      sqlu"delete from Student_emailAddresses".execute
      sqlu"delete from Student".execute


      sqlu"insert into Student (id,username,tokens) values ($studentId,'cindy',10)".execute
      sqlu"insert into Student (id,username,tokens) values ($studentWithZeroTokenId,'narendra',0)".execute
      val expDateInLessThanMonth = new java.sql.Date(LocalDate.now().plusMonths(1).minusDays(1).toDate.getTime)
      sqlu"insert into Student (id,username,tokens,expDate) values ($studentWithExpiringCard,'john',10,$expDateInLessThanMonth)".execute
      val expDateInOneMonth = new java.sql.Date(LocalDate.now().plusMonths(1).plusDays(1).toDate.getTime)
      sqlu"insert into Student (id,username,tokens,expDate) values ($studentWithGoodCard,'ahmet',10,$expDateInOneMonth)".execute



    }
  }


}
