import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


public class Main {

    public static void main(String args[ ]) throws Exception {

        Result result_ORM_OWL = JUnitCore.runClasses(ORM_to_OWL_Base_Methods_Testing.class);
        System.out.println("Количество выполненных тестов: " + result_ORM_OWL.getRunCount());
        System.out.println("Количество проваленных тестов: " + result_ORM_OWL.getFailureCount());
        for (Failure failure : result_ORM_OWL.getFailures()) {
            System.out.println(failure.toString());
        }

//        Result result_OWL_ORM = JUnitCore.runClasses(OWL_to_ORM_Testing.class);
//        System.out.println("Количество выполненных тестов: " + result_OWL_ORM.getRunCount());
//        for (Failure failure : result_OWL_ORM.getFailures())
//        {
//            System.out.println(failure.toString());
//        }
    }
}