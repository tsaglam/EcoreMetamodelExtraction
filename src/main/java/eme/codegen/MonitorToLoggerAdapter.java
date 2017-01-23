package eme.codegen;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.Monitor;

/**
 * Adapter class to feed monitor tasks and their messages into a log4j logger.
 * @author Timur Saglam
 */
public class MonitorToLoggerAdapter implements Monitor {
    private final Logger logger;
    private boolean canceled;

    /**
     * Basic constructor, sets the logger.
     * @param logger is the logger to feed the tasks and their messages.
     */
    public MonitorToLoggerAdapter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public void setCanceled(boolean value) {
        canceled = value;
    }

    @Override
    public void setBlocked(Diagnostic reason) {
        // Does nothing.
    }

    @Override
    public void clearBlocked() {
        // Does nothing.
    }

    @Override
    public void beginTask(String name, int totalWork) {
        if (name != null && name.length() > 1) {
            logger.debug(name);
        }
    }

    @Override
    public void setTaskName(String name) {
        // Does nothing.
    }

    @Override
    public void subTask(String name) {
        if (name != null && name.length() > 1) {
            logger.debug(name);
        }
    }

    @Override
    public void worked(int work) {
        // Does nothing.
    }

    @Override
    public void internalWorked(double work) {
        // Does nothing.
    }

    @Override
    public void done() {
        // Does nothing.
    }
}