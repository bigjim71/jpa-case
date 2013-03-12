package service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.JPAUtil;


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



}
