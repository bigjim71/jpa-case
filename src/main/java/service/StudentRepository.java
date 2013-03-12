package service;

import domain.Student;
import util.JPAUtil;
import util.Option;
import util.Some;

import javax.persistence.EntityManager;

/**
 * todo
 */
public class StudentRepository {


  private static StudentRepository instance = new StudentRepository();

  public static StudentRepository getInstance() {
    return instance;
  }

  private StudentRepository() {
  }


  public void insert(Student student) {
    EntityManager entityManager = JPAUtil.getEntityManager();
    entityManager.persist(student);


  }

  public Option<Student> getStudent(long studentId) {
    EntityManager entityManager = JPAUtil.getEntityManager();
    Student studentOrNull = entityManager.find(Student.class, studentId);
    if (studentOrNull == null) return util.None.none ();
    Student student = studentOrNull;
    return new Some(student);


  }
}
