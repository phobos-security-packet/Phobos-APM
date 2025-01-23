package mr.demonid.gui;

import mr.demonid.gui.presenter.Presenter;
import mr.demonid.gui.view.ViewSwing;

import javax.swing.*;


public class App
{
//    static String json = "{\"date\":[2024,11,13,10,20,47,237533400],\"repeater\":1,\"key\":103,\"code\":119,\"desc\":\"no desc\",\"formattedTime\":\"10:20:47\"}";

    public static void main( String[] args ) {

//        HttpSender httpSender = new HttpSender("/api/transfer/awp/send-to-dev", 9010);
//        httpSender.sendToDevice(new TMessage(2, 203, 119, "No desc"));

        SwingUtilities.invokeLater(() -> {
            new Presenter(new ViewSwing());
        });
    }

}
