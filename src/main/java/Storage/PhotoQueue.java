package Storage;

import org.glassfish.grizzly.utils.Pair;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.util.*;

public class PhotoQueue {
    static Queue<Pair<Pair<Long,java.io.File>, Set<String>>> queue = new LinkedList<Pair<Pair<Long,java.io.File>,Set<String>>>();
    private static PhotoQueue queueInstance = null;

    public static PhotoQueue getStreamInstance() {

        if (queueInstance == null) {
            queueInstance = new PhotoQueue();
        }
        return queueInstance;
    }

    public Queue<Pair<Pair<Long,java.io.File>, Set<String>>> get() {
        return queue;
    }

    public java.io.File getPhotoOfId(long chatId) {
        java.io.File res = null;
        for(Pair<Pair<Long,java.io.File>, Set<String>> ele: queue){
            if(ele.getFirst().getFirst().equals(chatId)) {
                res = ele.getFirst().getSecond();

            }

        }
        return res;
    }

    public void add(Pair<Pair<Long,java.io.File>, Set<String>> value){
        synchronized (queue){
            queue.add(value);
        }
    }

    public void addToAlreadySentSet(long chatId,String value) {
        for(Pair<Pair<Long,java.io.File>, Set<String>> ele: queue){
            if(ele.getFirst().getFirst().equals(chatId)) {
                ele.getSecond().add(value);
            }
        }
    }

    public Set<String> getAlreadySentSet(long chatId){
        Set<String> res = new HashSet<>();
        for(Pair<Pair<Long,java.io.File>, Set<String>> ele: queue){
            if(ele.getFirst().getFirst().equals(chatId)) {
                res = ele.getSecond();
            }
        }
        return res;
    }


    public void removePhotoFromQueue(long chatId){
        for(Pair<Pair<Long,java.io.File>, Set<String>> ele: queue) {
            if (ele.getFirst().getFirst().equals(chatId)) {
                queue.remove(ele);
            }
        }
    }

    public Pair<Pair<Long,java.io.File>, Set<String>> poll() {
        Pair<Pair<Long,java.io.File>, Set<String>> data = queue.poll();
        return data;
    }


    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int getTotalSize() {
        return queue.size();
    }
}

