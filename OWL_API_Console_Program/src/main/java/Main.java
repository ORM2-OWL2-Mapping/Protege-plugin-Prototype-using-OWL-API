import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.io.File;
import java.io.PrintStream;


public class Main {

    public static void main(String args[ ]) throws Exception {

        PrintStream console = System.out;

        Result result_ORM_OWL = JUnitCore.runClasses(ORM_OWL_Testing.class);
        Result result_OWL_ORM = JUnitCore.runClasses(OWL_ORM_Testing.class);

        PrintStream o = new PrintStream(new File("testingResults.txt"));

        System.setOut(o);

        System.out.println("Результаты тестирования");
        System.out.println("");

        System.out.println("Тестирование ORM -> OWL");
        System.out.println("Количество выполненных тестов: " + result_ORM_OWL.getRunCount());
        System.out.println("Количество пройденных тестов: " + (result_ORM_OWL.getRunCount() - result_ORM_OWL.getFailureCount()));
        System.out.println("Количество проваленных тестов: " + result_ORM_OWL.getFailureCount());
        if (result_ORM_OWL.getFailureCount() > 0) {
            System.out.println("Список проваленных тестов: ");
            for (Failure failure : result_ORM_OWL.getFailures()) {
                System.out.println(failure.getTestHeader());
            }
        }

        System.out.println("");
        System.out.println("Тестирование OWL -> ORM");
        System.out.println("Количество выполненных тестов: " + result_OWL_ORM.getRunCount());
        System.out.println("Количество пройденных тестов: " + (result_OWL_ORM.getRunCount() - result_OWL_ORM.getFailureCount()));
        System.out.println("Количество проваленных тестов: " + result_OWL_ORM.getFailureCount());
        if (result_OWL_ORM.getFailureCount() > 0) {
            System.out.println("Список проваленных тестов: ");
            for (Failure failure : result_OWL_ORM.getFailures()) {
                System.out.println(failure.getTestHeader());
            }
        }
    }
}