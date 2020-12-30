package dimitrikvirik.git;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        //ვქმნით წერილების მასივს და ვამატებთ
        List<Message> messageList = new ArrayList<>();
        messageList.add(new Message("Dimitri", "Putin", "fuck you", "2016-03-04 11:30:10"));
        messageList.add(new Message("Dimitri", "Trump", "kill joe biden", "2020-04-10 12:11:11"));
        messageList.add(new Message("Dimitri", "Vakho", "gigzavnit sakontrolos", "2020-12-12 15:00:00"));
        //ვწერთ წერილებს საქაღალდეში არასორტირებულად
        for (Message message : messageList) {
            Message.File.writeMessage(message, "unsorted");
        }
        //ვკითხულობთ ფაილებს
        List<Message> messages = Message.File.getMessage("unsorted");
        //ვწერთ წერილებს საქაღალდეში სორტირებულად
        for (Message message2 : messages) {
            Message.File.sortMessage(message2, "sorted");
        }
    }
}
