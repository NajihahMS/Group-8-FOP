package GoldenHour; 

/**
 * Polymorphism
 * - parent type
 * - SearchInfo and EditInfo EXTEND this.
 * - OVERRIDE run() with different behavior.
 */
public abstract class Info {

    // This method will be overridden by child classes
    // call run() using InfoModule reference.
    public abstract void run();
}


