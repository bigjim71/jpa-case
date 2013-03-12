package util;

import javassist.util.proxy.MethodFilter;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * todo
 */
public class JPAUtil {
  private static Logger logger = LoggerFactory.getLogger(JPAUtil.class);
  public static final EntityManagerFactory emf;

  static {
    Thread.currentThread().setContextClassLoader(Persistence.class.getClassLoader()); // scala REPL hack
    emf = Persistence.createEntityManagerFactory("StudentManagement");
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        emf.close();
      }
    });
  }

  public static void init(){
    // do nothing, loading of class initializes
  }

  private static ThreadLocal<EntityManager> entityManagerThreadLocal = new ThreadLocal<EntityManager>();

  public static EntityManager getEntityManager() {
    EntityManager entityManager = entityManagerThreadLocal.get();
    if (entityManager == null) {
      entityManager = emf.createEntityManager();
      entityManagerThreadLocal.set(entityManager);
    }
    return entityManager;

  }

  public static <T> T getTransactionalProxy(Class<? extends T> clazz) {
    ProxyFactory f = new ProxyFactory();
    f.setSuperclass(clazz);
    f.setFilter(new MethodFilter() {

      public boolean isHandled(Method m) {
        return Modifier.isPublic(m.getModifiers());
      }
    });
    Class c = f.createClass();
    MethodHandler mi = new MethodHandler() {


      public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        final EntityManager entityManager = getEntityManager();
        try {
          final EntityTransaction transaction = entityManager.getTransaction();
          boolean iOwnTransaction = !transaction.isActive();
          if (iOwnTransaction) {
            transaction.begin();
          }
          Object returnValue = null;
          try {
            returnValue = proceed.invoke(self, args);
            if (iOwnTransaction) {
              if (transaction.getRollbackOnly()) {
                logger.info("Rolling back transaction, as flag has been set");
                transaction.rollback();
              } else
                transaction.commit();
            }
          } catch (InvocationTargetException e) {

            if (iOwnTransaction) {
              logger.info("Rolled back transaction due to exception", e);
              transaction.rollback();
            } else {
              logger.info("Setting rollback-only flag due to exception", e);
              transaction.setRollbackOnly();
            }
            throw e.getCause();
          }
          return returnValue;
        } finally {
          if (entityManager.isOpen()) {
            entityManager.close();
            entityManagerThreadLocal.remove();
          }
        }


      }
    };
    final T instance;
    try {
      instance = (T) c.newInstance();
      ((ProxyObject) instance).setHandler(mi);
      return instance;
    } catch (InstantiationException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);

    }

  }

}