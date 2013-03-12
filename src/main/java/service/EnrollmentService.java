package service;

import domain.BookingException;
import domain.Student;
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

  public long registerStudent(String username) {
    Student newStudent = new Student(username);
    studentRepository.insert(newStudent);
    return newStudent.getId();

  }

  public void registerStudentForEvent(long studentId, String title) throws BookingException {
    Option<Student> studentOption = studentRepository.getStudent(studentId);
    Student student = studentOption.getValueOrThrowException(BookingException.class, "Unknown student " + studentId);
    student.registerForEvent(title);
  }

}
