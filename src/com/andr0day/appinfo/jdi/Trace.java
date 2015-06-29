package com.andr0day.appinfo.jdi;

import com.sun.jdi.*;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.event.*;
import com.sun.jdi.request.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Trace {

    // Running remote VM
    private final VirtualMachine vm;

    // Mode for tracing the Trace program (default= 0 off)
    private int debugTraceMode = 0;

    /**
     * main
     */
    public static void main(String[] args) {
        new Trace(args);
    }

    /**
     * Parse the command line arguments. Launch target VM. Generate the trace.
     */
    public Trace(String[] args) {
        PrintWriter writer = new PrintWriter(System.out);
        int inx;
        for (inx = 0; inx < args.length; ++inx) {
            String arg = args[inx];
            if (arg.charAt(0) != '-') {
                break;
            }
            if (arg.equals("-output")) {
                try {
                    writer = new PrintWriter(new FileWriter(args[++inx]));
                } catch (IOException exc) {
                    System.err.println("Cannot open output file: " + args[inx]
                            + " - " + exc);
                    System.exit(1);
                }
            }
        }
        if (inx >= args.length) {
            System.err.println("<class> missing");
            usage();
            System.exit(1);
        }
        StringBuffer sb = new StringBuffer();
        sb.append(args[inx]);
        for (++inx; inx < args.length; ++inx) {
            sb.append(' ');
            sb.append(args[inx]);
        }
        vm = launchTarget(sb.toString());
        generateTrace(writer);
    }

    /**
     * Generate the trace. Enable events, start thread to display events, start
     * threads to forward remote error and output streams, resume the remote VM,
     * wait for the final event, and shutdown.
     */
    void generateTrace(PrintWriter writer) {
        vm.setDebugTraceMode(debugTraceMode);
        EventThread eventThread = new EventThread(vm, writer);
        eventThread.setEventRequests();
        eventThread.start();
        vm.resume();

        // Shutdown begins when event thread terminates
        try {
            eventThread.join();
        } catch (InterruptedException exc) {
            // we don't interrupt
        }
        writer.close();
    }

    /**
     * Launch target VM. Forward target's output and error.
     */
    VirtualMachine launchTarget(String mainArgs) {
        mainArgs = mainArgs.trim();
        LaunchingConnector connector = findLaunchingConnector();
        Map arguments = connectorArguments(connector, mainArgs);
        try {
            return connector.launch(arguments);
        } catch (IOException exc) {
            throw new Error("Unable to launch target VM: " + exc);
        } catch (IllegalConnectorArgumentsException exc) {
            throw new Error("Internal error: " + exc);
        } catch (VMStartException exc) {
            throw new Error("Target VM failed to initialize: "
                    + exc.getMessage());
        }
    }

    /**
     * Find a com.sun.jdi.CommandLineLaunch connector
     */
    LaunchingConnector findLaunchingConnector() {
        List connectors = Bootstrap.virtualMachineManager().allConnectors();
        Iterator iter = connectors.iterator();
        while (iter.hasNext()) {
            Connector connector = (Connector) iter.next();
            if ("com.sun.jdi.CommandLineLaunch".equals(connector.name())) {
                return (LaunchingConnector) connector;
            }
        }
        throw new Error("No launching connector");
    }

    /**
     * Return the launching connector's arguments.
     */
    Map connectorArguments(LaunchingConnector connector, String mainArgs) {
        Map arguments = connector.defaultArguments();
        Connector.Argument mainArg = (Connector.Argument) arguments.get("main");
        if (mainArg == null) {
            throw new Error("Bad launching connector");
        }
        mainArg.setValue(mainArgs);
        return arguments;
    }

    /**
     * Print command line usage help
     */
    void usage() {
        System.err.println("Usage: java Trace <options> <class> <args>");
        System.err.println("<options> are:");
        System.err.println("  -output <filename>   Output trace to <filename>");
    }
}

class EventThread extends Thread {

    private final VirtualMachine vm; // Running VM

    private final String[] excludes; // Packages to exclude

    private final PrintWriter writer; // Where output goes

    private boolean connected = true; // Connected to VM

    private boolean vmDied = true; // VMDeath occurred

    // Maps ThreadReference to ThreadTrace instances
    private Map<ThreadReference, ThreadTrace> traceMap = new LinkedHashMap<ThreadReference, ThreadTrace>();

    EventThread(VirtualMachine vm, PrintWriter writer) {
        super("event-handler");
        this.vm = vm;
        this.excludes = ClassPatternFilter.getExcludes();
        this.writer = writer;
    }

    /**
     * Run the event handling thread. As long as we are connected, get event
     * sets off the queue and dispatch the events within them.
     */
    public void run() {
        EventQueue queue = vm.eventQueue();
        while (connected) {
            try {
                EventSet eventSet = queue.remove();
                EventIterator it = eventSet.eventIterator();
                while (it.hasNext()) {
                    handleEvent(it.nextEvent());
                }
                eventSet.resume();
            } catch (InterruptedException exc) {
                // Ignore
            } catch (VMDisconnectedException discExc) {
                handleDisconnectedException();
                break;
            }
        }
    }

    /**
     * Register events we want
     * Create the desired event requests, and enable them so that we will get
     * events.
     *
     * @param excludes    Class patterns for which we don't want events
     * @param watchFields Do we want to watch assignments to fields
     */
    void setEventRequests() {
        EventRequestManager mgr = vm.eventRequestManager();
        // want all exceptions
        ExceptionRequest excReq = mgr.createExceptionRequest(null, true, true);
        // suspend so we can step
        excReq.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
        excReq.enable();

        MethodEntryRequest menr = mgr.createMethodEntryRequest();
        for (int i = 0; i < excludes.length; ++i) {
            menr.addClassExclusionFilter(excludes[i]);
        }
        menr.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
        menr.enable();

        MethodExitRequest mexr = mgr.createMethodExitRequest();
        for (int i = 0; i < excludes.length; ++i) {
            mexr.addClassExclusionFilter(excludes[i]);
        }
        mexr.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
        mexr.enable();

        ThreadStartRequest tsr = mgr.createThreadStartRequest();
        // Make sure we sync on thread death
        tsr.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
        tsr.enable();

        ThreadDeathRequest tdr = mgr.createThreadDeathRequest();
        // Make sure we sync on thread death
        tdr.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
        tdr.enable();
    }

    /**
     * This class keeps context on events in one thread. In this implementation,
     * context is the indentation prefix.
     */
    class ThreadTrace {
        final ThreadReference thread;

        String baseIndent = "   ";

        static final String threadDelta = "\t";

        StringBuffer logRecord;

        Stack<Method> methodStack = new Stack<Method>();

        ThreadTrace(ThreadReference thread) {
            this.thread = thread;
            logRecord = new StringBuffer(256);
            println("====== " + thread.name() + " ======");
        }

        private void println(String str) {
            logRecord.append(this.baseIndent + str);
            logRecord.append(System.getProperty("line.separator"));
        }

        String getLogRecord() {
            return logRecord.toString();
        }

        void methodEntryEvent(MethodEntryEvent event) {
            methodStack.push(event.method());
            increaseIndent();
            println("Enter Method:" + event.method().name());
            this.printVisiableVariables();

        }

        void methodExitEvent(MethodExitEvent event) {
            println("Exit Method:" + event.method().name());
            decreaseIndent();
            methodStack.pop();
        }

        void exceptionEvent(ExceptionEvent event) {
            increaseIndent();
            println(event.exception() + " catch: "
                    + event.catchLocation());
            increaseIndent();
            printStackSnapShot();
            decreaseIndent();
            decreaseIndent();
            // Step to the catch
            EventRequestManager mgr = vm.eventRequestManager();
            StepRequest req = mgr.createStepRequest(thread,
                    StepRequest.STEP_MIN, StepRequest.STEP_INTO);
            req.addCountFilter(1); // next step only
            req.setSuspendPolicy(EventRequest.SUSPEND_ALL);
            req.enable();
        }

        // Step to exception catch
        void stepEvent(StepEvent event) {
            //when exception happens, adjust the indent
            while (methodStack.capacity() > 0 && methodStack.peek() != event.location().method()) {
                this.decreaseIndent();
                methodStack.pop();
            }
            EventRequestManager mgr = vm.eventRequestManager();
            mgr.deleteEventRequest(event.request());
        }

        void threadDeathEvent(ThreadDeathEvent event) {
            println("====== " + thread.name() + " end ======");
            println("");
        }

        void threadStartEvent(ThreadStartEvent event) {
            println("Thread " + event.thread().name() + " Start");
        }

        private void printVisiableVariables() {
            try {
                this.thread.suspend();
                if (this.thread.frameCount() > 0) {
                    //retrieve current method frame
                    StackFrame frame = this.thread.frame(0);
                    List<Field> fields = frame.thisObject().referenceType().allFields();
                    increaseIndent();
                    for (Field field : fields) {
                        println(field.name() + "\t"
                                + field.typeName()
                                + "\t" + frame.thisObject().getValue(field));
                    }
                    decreaseIndent();
                }
            } catch (Exception e) {
                //ignore
            } finally {
                this.thread.resume();
            }
        }

        private void printStackSnapShot() {
            if (isMainThreadOrCreatedFromMain(this.thread)) {
                try {
                    this.thread.suspend();
                    println("Thread Status:" + this.thread.status());
                    println("FrameCount in thread:" + this.thread.frameCount());
                    List<StackFrame> frames = this.thread.frames();
                    for (StackFrame frame : frames) {
                        println("Frame(" + frame.location()
                                + ")");
                        if (frame.thisObject() != null) {
                            increaseIndent();
                            println("");
                            List<Field> fields = frame.thisObject()
                                    .referenceType().allFields();
                            for (Field field : fields) {
                                println(field.name() + "\t"
                                        + field.typeName()
                                        + "\t"
                                        + frame.thisObject().getValue(field));
                            }
                            decreaseIndent();
                        }
                        List<LocalVariable> lvs = frame.visibleVariables();
                        increaseIndent();
                        println("");
                        for (LocalVariable lv : lvs) {
                            println(lv.name() + "\t"
                                    + lv.typeName() + "\t"
                                    + frame.getValue(lv));
                        }
                        decreaseIndent();
                    }
                } catch (Exception e) {
                    // ignore the exception
                } finally {
                    this.thread.resume();
                }
            }
        }

        public void increaseIndent() {
            baseIndent += threadDelta;
        }

        public void decreaseIndent() {
            baseIndent = baseIndent.substring(0, baseIndent.length() - threadDelta.length());
        }
    }

    /**
     * Returns the ThreadTrace instance for the specified thread, creating one
     * if needed.
     */
    ThreadTrace threadTrace(ThreadReference thread) {
        ThreadTrace trace = (ThreadTrace) traceMap.get(thread);
        if (trace == null) {
            trace = new ThreadTrace(thread);
            traceMap.put(thread, trace);
        }
        return trace;
    }

    /**
     * Dispatch incoming events
     */
    private void handleEvent(Event event) {
        if (event instanceof ExceptionEvent) {
            if (ExceptionFilter.isAllowedException(((ExceptionEvent) event)
                    .exception().referenceType().name())) {
                exceptionEvent((ExceptionEvent) event);
            }
        } else if (event instanceof MethodEntryEvent) {
            if (isMainThreadOrCreatedFromMain(((MethodEntryEvent) event)
                    .thread())) {
                methodEntryEvent((MethodEntryEvent) event);
            }
        } else if (event instanceof MethodExitEvent) {
            if (isMainThreadOrCreatedFromMain(((MethodExitEvent) event)
                    .thread())) {
                methodExitEvent((MethodExitEvent) event);
            }
        } else if (event instanceof StepEvent) {
            stepEvent((StepEvent) event);
        } else if (event instanceof ThreadStartEvent) {
            threadStartEvent((ThreadStartEvent) event);
        } else if (event instanceof ThreadDeathEvent) {
            threadDeathEvent((ThreadDeathEvent) event);
        } else if (event instanceof VMStartEvent) {
            vmStartEvent((VMStartEvent) event);
        } else if (event instanceof VMDeathEvent) {
            vmDeathEvent((VMDeathEvent) event);
        } else if (event instanceof VMDisconnectEvent) {
            vmDisconnectEvent((VMDisconnectEvent) event);
        } else {
            throw new Error("Unexpected event type");
        }
    }

    /**
     * ************************************************************************
     * A VMDisconnectedException has happened while dealing with another event.
     * We need to flush the event queue, dealing only with exit events (VMDeath,
     * VMDisconnect) so that we terminate correctly.
     */
    synchronized void handleDisconnectedException() {
        EventQueue queue = vm.eventQueue();
        while (connected) {
            try {
                EventSet eventSet = queue.remove();
                EventIterator iter = eventSet.eventIterator();
                while (iter.hasNext()) {
                    Event event = iter.nextEvent();
                    if (event instanceof VMDeathEvent) {
                        vmDeathEvent((VMDeathEvent) event);
                    } else if (event instanceof VMDisconnectEvent) {
                        vmDisconnectEvent((VMDisconnectEvent) event);
                    }
                }
                eventSet.resume(); // Resume the VM
            } catch (InterruptedException exc) {
                // ignore
            }
        }
    }

    private void vmStartEvent(VMStartEvent event) {
        writer.println("-- VM Started --");
    }

    // Forward event for thread specific processing
    private void methodEntryEvent(MethodEntryEvent event) {
        threadTrace(event.thread()).methodEntryEvent(event);
    }

    // Forward event for thread specific processing
    private void methodExitEvent(MethodExitEvent event) {
        threadTrace(event.thread()).methodExitEvent(event);
    }

    // Forward event for thread specific processing
    private void stepEvent(StepEvent event) {
        threadTrace(event.thread()).stepEvent(event);
    }

    void threadDeathEvent(ThreadDeathEvent event) {
        ThreadTrace trace = (ThreadTrace) traceMap.get(event.thread());
        if (trace != null) { // only want threads we care about
            trace.threadDeathEvent(event); // Forward event
        }
    }

    void threadStartEvent(ThreadStartEvent event) {
        threadTrace(event.thread()).threadStartEvent(event);
    }

    private void exceptionEvent(ExceptionEvent event) {
        threadTrace(event.thread()).exceptionEvent(event);
    }

    public void vmDeathEvent(VMDeathEvent event) {
        vmDied = true;
        printAllThreadInfos();
        writer.println("-- The application exited --");
    }

    public void vmDisconnectEvent(VMDisconnectEvent event) {
        connected = false;
        if (!vmDied) {
            writer.println("-- The application has been disconnected --");
        }
    }

    public void printAllThreadInfos() {
        Set<ThreadReference> threadSet = traceMap.keySet();
        for (ThreadReference thread : threadSet) {
            writer.println(traceMap.get(thread).getLogRecord());
            writer
                    .println("*********************************************************");
        }
    }

    public boolean isMainThreadOrCreatedFromMain(ThreadReference tr) {
        if (tr == null || "system".equalsIgnoreCase(tr.name())) {
            return false;
        }
        if ("main".equalsIgnoreCase(tr.name())
                || "main".equalsIgnoreCase(tr.threadGroup().name())) {
            return true;
        }
        return false;
    }
}
