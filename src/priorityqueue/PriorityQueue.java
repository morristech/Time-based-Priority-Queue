
package priorityqueue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;


public class PriorityQueue {

    ArrayList<Task> tasks = new ArrayList<Task>();
    public static void main(String[] args) throws Exception{
        PriorityQueue queue = new PriorityQueue();
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter output fileName, input fileName and timestamp :");
	String ifileName = sc.next();
        String ofileName = sc.next();
        String now = sc.next();
        String time = sc.next();
        now = now.replaceAll("\"", "");
        time = time.replaceAll("\"", "");
        String present = now +" "+ time;
        File file = new File(ofileName);
        BufferedWriter writer = null;
        List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm", Locale.ENGLISH);
        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm", Locale.ENGLISH);
        for(String line : lines){
            if(line.contains("priority")) continue;
            String[] array = line.split(",");
            Task t;
            if(array.length == 3){
                String date = array[1].replace("\"", "");
                Date d = dFormat.parse(date);
                t = new Task(array[0], d, Integer.parseInt(array[2].trim()));
            }
            else{
                String date = array[1].replace("\"", "");
                Date d = dFormat.parse(date);
                t = new Task(array[0], d, 0);
            }
            queue.tasks.add(t);
        }
        Collections.sort(queue.tasks, new Comparator<Task>(){
            @Override
            public int compare(Task o1, Task o2) {
                return o1.getTime().compareTo(o2.getTime());
            }
            
        });
        Collections.sort(queue.tasks, new Comparator<Task>(){
            @Override
            public int compare(Task o1, Task o2) {
                if(o1.getTime() == o2.getTime()){
                    if(o1.getPriority() > o2.getPriority())
                        return 1;
                    else if(o1.getPriority() == o2.getPriority())
                        return 0;
                    else return -1;
                }
                else return 0;
            }
        });
        try{
            Date tempDate = queue.tasks.get(0).getTime();
            long diff;
            diff = tempDate.getTime() - dFormat.parse(present).getTime();
            diff = diff / (60 * 1000);
            File oFile = new File(ifileName+".txt");
            if (!oFile.exists()) {
                oFile.createNewFile();
            }
            writer = new BufferedWriter(new FileWriter(oFile.getAbsoluteFile()));
            writer.write("-- After "+diff+" minute --\n");
            for (Iterator<Task> iterator = queue.tasks.iterator(); iterator.hasNext();) {
                Task task = iterator.next();
                if(tempDate.getTime() != task.getTime().getTime()){
                    if(tempDate.getTime() > task.getTime().getTime())
                        diff = tempDate.getTime() - task.getTime().getTime();
                    else
                        diff = task.getTime().getTime() - tempDate.getTime();
                    diff = diff / (60 * 1000);
                    writer.write("-- After "+diff+" minute --"+"\n");
                    writer.write("Current time [ "+dFormat.format(task.getTime())+" ] , Event "+task.getTask()+" Processed"+"\n");
                    tempDate = task.getTime();
                }
                else if(tempDate.getTime() == task.getTime().getTime()){
                    writer.write("Current time [ "+dFormat.format(task.getTime())+" ] , Event "+task.getTask()+" Processed"+"\n");
                }
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally{
            try{
                writer.flush();
                writer.close();
            }
            catch (Exception ex) {
                
            }
        }
    }
}
