package service;

import domain.BookingException;
import domain.Student;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.JPAUtil;
import util.Option;


/**
 * todo
 */
public class EnrollmentService {
  private static Logger logger = LoggerFactory.getLogger(EnrollmentService.class);

  StudentRepository studentRepository = StudentRepository.getInstance();
  private static EnrollmentService instance = JPAUtil.getTransactionalProxy(EnrollmentService.class);

  public static EnrollmentService getInstance(){
    return instance;
  }


  protected EnrollmentService() {
  }

  public long registerStudent(String username, String password, String creditCardNumber, LocalDate expDate, String initialEmail) {
    if (username==null) throw new IllegalArgumentException("username is null");
    if (creditCardNumber==null) throw new IllegalArgumentException("creditCardNumber is null");
    if (expDate==null) throw new IllegalArgumentException("creditCardNumber is null");
    if (initialEmail==null) throw new IllegalArgumentException("creditCardNumber is null");


    Student newStudent = new Student(username, password, initialEmail);
    newStudent.useCard(creditCardNumber,expDate);


    studentRepository.insert(newStudent);
    return newStudent.getId();

  }

  public void registerStudentForEvent(long studentId, String title) throws BookingException {
    if (title==null) throw new IllegalArgumentException("title is null");
    Option<Student> studentOption = studentRepository.getStudent(studentId);
    Student student = studentOption.getValueOrThrowException(BookingException.class, "Unknown student " + studentId);
    student.registerForEvent(title);
  }

}
