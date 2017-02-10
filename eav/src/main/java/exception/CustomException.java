package exception;

@SuppressWarnings("serial")
public class CustomException extends Exception {

    private String msg;

    private StackTraceElement[] stackTrace;

    public CustomException(String msg) {
        this(msg, null);
    }

    public CustomException(String msg, StackTraceElement[] stackTrace) {
        super();

        this.msg = msg;
        this.stackTrace = stackTrace;
    }

    public String getMessage() { return msg; }

    public StackTraceElement[] getStackTrace() { return stackTrace; }

}
