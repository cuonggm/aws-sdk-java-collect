package com.cuonggm;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    /**
     * Says hello to the world.
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        if(args.length == 0) {
            log("args=0");
            return;
        }

        for(String param : args) {
            log(param);
        }
    }

    public static void log(String message) {
        System.out.println(message);
    }
}
