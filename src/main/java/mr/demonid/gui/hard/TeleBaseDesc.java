package mr.demonid.gui.hard;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Базовый класс для сопоставления соответствий кодов событий и их описания
 */
public abstract class TeleBaseDesc implements Iterable<TeleDescription> {

    protected ArrayList<TeleDescription> elem;

    public TeleBaseDesc()
    {
        elem = new ArrayList<>();
    }

    public String getDescription(int code)
    {
        for (TeleDescription msg : elem) {
            if (msg.getCode() == code)
                return msg.getDescription();
        }
        return null;
    }

    public String getShortDescription(int code) {
        for (TeleDescription msg : elem) {
            if (msg.getCode() == code)
                return msg.getShortDescription();
        }
        return null;
    }

    public int getCode(String description) {
        for (TeleDescription msg : elem) {
            if (description.equals(msg.getDescription()))
            {
                return msg.getCode();
            }
        }
        return 0;
    }

    public int getLevel(int code)
    {
        for (TeleDescription msg : elem)
        {
            if (msg.getCode() == code)
                return msg.getLevel();
        }
        return -1;
    }


    /*
    ================================================================
    Реализация интерфейса Iterable
    ================================================================
     */

    public Iterator<TeleDescription> iterator() {
        return new TeleIterator();
    }

    class TeleIterator implements Iterator<TeleDescription> {

        int index;

        public TeleIterator() {
            this.index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < elem.size();
        }

        @Override
        public TeleDescription next() {
            TeleDescription res = null;
            if (hasNext())
            {
                res = elem.get(index++);
            }
            return res;
        }
    }
}
