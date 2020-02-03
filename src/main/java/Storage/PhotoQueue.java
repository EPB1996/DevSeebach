package Storage;

import org.glassfish.grizzly.utils.Pair;
import org.glassfish.jersey.server.internal.inject.ParamInjectionResolver;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class PhotoQueue {
    static Queue<Pair<Long,java.io.File>> queue = new LinkedList<Pair<Long,java.io.File>>();
    private static PhotoQueue queueInstance = null;

    public static PhotoQueue getStreamInstance() {

        if (queueInstance == null) {
            queueInstance = new PhotoQueue();
        }
        return queueInstance;
    }

    public Queue<Pair<Long,java.io.File>> get() {
        return queue;
    }

    public java.io.File getPhotoOfId(long chatId) {
        java.io.File res = null;
        for(Pair<Long,java.io.File> ele: queue){
            if(ele.getFirst().equals(chatId)) {
                res = ele.getSecond();
                queue.remove(ele);
            }

        }
        return res;
    }


    public void add(Pair<Long,java.io.File> value) {
        synchronized (queue) {
            queue.add(value);
        }
    }



    public Pair<Long,java.io.File> poll() {
        Pair<Long,java.io.File> data = queue.poll();
        return data;
    }


    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int getTotalSize() {
        return queue.size();
    }
}

