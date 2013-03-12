package service;

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



}
