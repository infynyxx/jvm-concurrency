package com.infynyxx.jvmconcurrency;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Actors;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Prajwal Tuladhar <praj@infynyxx.com>
 */

class RequestAFile {}

class FileSize {
    public final long size;
    public FileSize(final long size) {
        this.size = size;
    }
}

class FileToProcess {
    public final String fileName;
    public FileToProcess(final String fileName) {
        this.fileName = fileName;
    }
}


class FileProcessor extends UntypedActor {
    
    private final ActorRef sizeCollector;
    
    public FileProcessor(final ActorRef sizeCollector) {
        this.sizeCollector = sizeCollector;
    }

    @Override
    public void onReceive(final Object message) throws Exception {
        FileToProcess fileToProcess = (FileToProcess) message;
        final File file = new File(fileToProcess.fileName);
        long size = 0L;
        if (file.isFile()) {
            size = file.length();
        } else {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (child.isFile()) {
                        size += child.length();
                    } else {
                        sizeCollector.tell(new FileToProcess(file.getPath()));
                    }
                }
            }
        }
        sizeCollector.tell(new FileSize(size));
        registerToGetFile();
    }
    
    @Override
    public void preStart() {
        
    }
    
    public void registerToGetFile() {
        sizeCollector.tell(new RequestAFile(), getContext());
    }
    
}


class SizeCollector extends UntypedActor {
    
    private final List<String> toProcessFileNames = new ArrayList<String>();
    private final List<ActorRef> idleFileProcessors = new ArrayList<ActorRef>();
    private long pendingNumberOfFilesToVisit = 0L;
    private long totalSize = 0L;
    private long start = System.nanoTime();
    
    public void sendAFileToProcess() {
        if (!toProcessFileNames.isEmpty() && !idleFileProcessors.isEmpty()) {
            idleFileProcessors.remove(0).tell(new FileToProcess(toProcessFileNames.remove(0)));
        }
    }

    @Override
    public void onReceive(final Object message) throws Exception {
        if (message instanceof RequestAFile) {
            idleFileProcessors.add(getContext().getSender().get());
            sendAFileToProcess();
        } else if (message instanceof FileToProcess) {
            toProcessFileNames.add(((FileToProcess)(message)).fileName);
            pendingNumberOfFilesToVisit += 1;
            sendAFileToProcess();
        } else if (message instanceof FileSize) {
            totalSize += ((FileSize)(message)).size;
            pendingNumberOfFilesToVisit -= 1;
            
            if (pendingNumberOfFilesToVisit == 0) {
                long end = System.nanoTime();
                System.out.println("Total Size is " + totalSize);
                System.out.println("Time taken is " + (end - start) / 1.0e9);
                Actors.registry().shutdownAll();
            }
        }
    }
    
}

public class ConcurrentFileSizeWAkka {
    public static void main(String[] args) {
        final ActorRef sizeCollector = Actors.actorOf(SizeCollector.class).start();
        sizeCollector.tell(new FileToProcess("/usr"));
        
        for (int i = 0; i < 100; i++) {
            Actors.actorOf(new UntypedActorFactory() {
                @Override
                public UntypedActor create() {
                    return new FileProcessor(sizeCollector);
                }
            }).start();
        }
    }
}


