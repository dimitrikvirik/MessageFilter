package dimitrikvirik.git;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Message implements Serializable {
    private String from; //ვინ
    private String to; // ვის
    private String text; //რა
    private LocalDateTime time;
    private MessageType messageType;

    private List<String> vulgarWords = new ArrayList<>();
    private List<String> dangerousWords = new ArrayList<>();
    public Message(String from, String to, String text, String time) {
        this.from = from;
        this.to = to;
        this.text = text;
        this.time = strToDateTime(time);
        defaultVulgarWords();
        defaultDangerousWords();

        this.messageType = ScanMessage();
    }
    /**
     * Class for working with Message files
     */
    static class File{
        /**
         * Write message to folder (unsorted)
         * @param message
         * @param root
         * @throws IOException
         */
        static public void writeMessage(Message message, String root) throws IOException {
            addIfNot(root);
            int randomNum = ThreadLocalRandom.current().nextInt(0, 100000 + 1);
            Path path = Paths.get(root+"/" + message.getFrom() + "_" + randomNum + ".txt");
            try(
                    FileOutputStream file = new FileOutputStream(String.valueOf(path));
                    ObjectOutputStream out = new ObjectOutputStream(file);
            ){
                out.writeObject(message);
            }
            Date date = convertToDateViaInstant(message.getTime());
            Files.setLastModifiedTime(path, FileTime.fromMillis(date.getTime()));
        }

        /**
         *  Write message to folder (sorted)
         * @param message
         * @param root
         * @throws IOException
         * @throws ParseException
         */
        static public void sortMessage(Message message, String root) throws IOException, ParseException {
            //Create Directory if Not Exist
            addIfNot(root);
            addIfNot(root+"/"+MessageType.SAFE.getName());
            addIfNot(root+"/"+MessageType.VULGAR.getName());
            addIfNot(root+"/"+MessageType.DANGEROUS.getName());
            int randomNum = ThreadLocalRandom.current().nextInt(0, 100000 + 1);
            Path path = Paths.get(root+"/" + message.getMessageType().getName() + "/" + message.getFrom() + "_" + randomNum + ".txt");
            try(
                    FileOutputStream file = new FileOutputStream(String.valueOf(path));
                    ObjectOutputStream out = new ObjectOutputStream(file);
            ){
                out.writeObject(message);
            }
            Date date = convertToDateViaInstant(message.getTime());
            Files.setLastModifiedTime(path, FileTime.fromMillis(date.getTime()));
        }

        /**
         * Get message List from folder
         * @param root
         * @return
         */
        static public List<Message> getMessage(String root){
            List<Message> messageList = new ArrayList<>();
            try (Stream<Path> paths = Files.walk(Paths.get(root+"/"))) {
                List<Path> pathList =  paths.filter(Files::isRegularFile).collect(Collectors.toList());
                for (Path path: pathList) {
                    // Deserialization
                    Message object1 = null;
                    try(
                            // Reading the object from a file
                            FileInputStream file = new FileInputStream(String.valueOf(path));
                            ObjectInputStream in = new ObjectInputStream(file);
                    )
                    {
                        // Method for deserialization of object
                        messageList.add((Message) in.readObject());

                    }
                    catch(IOException ex)
                    {
                        System.out.println("IOException is caught");
                    }
                    catch(ClassNotFoundException ex)
                    {
                        System.out.println("ClassNotFoundException is caught");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return messageList;
        }
        //LocaDateTime to Date
        private static Date convertToDateViaInstant(LocalDateTime dateToConvert) {
            return java.util.Date
                    .from(dateToConvert.atZone(ZoneId.systemDefault())
                            .toInstant());
        }
        //Create Directory if doesnot exist
        private static void addIfNot(String path) throws IOException {
            if(!Files.isDirectory(Paths.get(path))){
                Files.createDirectory(Paths.get(path));
            }
        }
    }

    /**
     * scan message if contains dangerous or vulgar words
     * @return MessageType
     */
    private MessageType ScanMessage(){
        for (String dangerousWord : dangerousWords) {
            if(this.text.contains(dangerousWord)){
                return MessageType.DANGEROUS;
            }
        }
        for (String vulgarWord : vulgarWords) {
            if(this.text.contains(vulgarWord)){
                return MessageType.VULGAR;
            }
        }
        return MessageType.SAFE;
    }

    /**
     * set default vulgar words in vulgarWords list
     */
    private void defaultVulgarWords(){
        String[] words = {"fuck", "shit", "f*ck", "sh*t", "poop", "sex", "dick"};
        Collections.addAll(vulgarWords, words);
    }
    /**
     * set default dangerous words in dangerousWords list
     */
    private void defaultDangerousWords(){
        String[] words = {"kill president", "kill someone", "kill joe biden"};
        dangerousWords.addAll(Arrays.asList(words));
    }

    /**
     * Convert string to LocalDateTime
     * @param str
     * @return LocalDateTime
     */
    private LocalDateTime strToDateTime(String str){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(str, formatter);
    }

/*
   STANDARD METHODS FOR CLASS
 */
    //Get Values
    public String getFrom() {
        return from;
    }
    public String getTo() {
        return to;
    }
    public String getText() {
        return text;
    }
    public LocalDateTime getTime() {
        return time;
    }
    public MessageType getMessageType() {
        return messageType;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public void setVulgarWords(String vulgarWord) {
        this.vulgarWords.add(vulgarWord);
    }

    public void setDangerousWords(String dangerousWord) {
        this.dangerousWords.add(dangerousWord);
    }

    @Override
    public String toString() {
        return "Message{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", text='" + text + '\'' +
                ", time=" + time +
                ", messageType=" + messageType +
                " }";
    }
}
