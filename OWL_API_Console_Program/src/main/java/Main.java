import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;


public class Main {

    public static void main(String args[ ]) throws Exception {

        Result result_ORM_OWL = JUnitCore.runClasses(ORM_OWL_Testing.class);
        System.out.println("Run Tests: " + result_ORM_OWL.getRunCount());
        System.out.println("Failed Tests: " + result_ORM_OWL.getFailureCount());
        for (Failure failure : result_ORM_OWL.getFailures()) {
            System.out.println(failure.toString());
        }

        Result result_OWL_ORM = JUnitCore.runClasses(OWL_ORM_Testing.class);
        System.out.println("Run Tests: " + result_OWL_ORM.getRunCount());
        System.out.println("Failed Tests: " + result_OWL_ORM.getFailureCount());
        for (Failure failure : result_OWL_ORM.getFailures()) {
            System.out.println(failure.toString());
        }
    }
}