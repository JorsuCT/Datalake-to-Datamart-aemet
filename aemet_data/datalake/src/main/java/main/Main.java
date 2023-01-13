package main;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller();
        String tabla_1 = "max_temp";
        String tabla_2 = "min_temp";
        controller.run(args[0], tabla_1, tabla_2);
    }
}
