import java.util.*;
import java.io.*;


public class IntervalPartitioning
{

    public static int classrooms = 0;
    private static ArrayList<Request> requests;
    private static PriorityQueue<Classroom> listOfClassrooms;
    private static ArrayList<Request> listForSchedule = requests;

    public static void main(String args[])
    {
        if(args.length == 0)
        {
            System.out.println("Please give us a filename");
            System.exit(0);
        }

        File file = new File(args[0]);
        BufferedReader reader = null;
        int numberOfrequests;


        try {
            reader = new BufferedReader(new FileReader(file));
            String text = null;

            while((text = reader.readLine()) != null){
                if(text.startsWith("#") || text.isEmpty()) continue;
                if(requests == null){
                    numberOfrequests = Integer.parseInt(text);
                    requests = new ArrayList<Request>(numberOfrequests);
                    listForSchedule = new ArrayList<Request>(numberOfrequests);

                }
                else {
                    String[] splits = text.split(" ");
                    Request j = new Request(Integer.parseInt(splits[0]), Integer.parseInt(splits[1]));
                    requests.add(j);
                    listForSchedule.add(j);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //PriorityQueue sorted on earliest endTime on classrooms
        listOfClassrooms = new PriorityQueue<>(new Comparator<Classroom>() {
            @Override
            public int compare(Classroom o1, Classroom o2) {
                return o1.endTime - o2.endTime;
            }
        });

        sortList(listForSchedule);
        DoSchedule(listForSchedule);

        System.out.println(classrooms+1 + "\n");
        for(int i=0; i< requests.size(); i++){
            System.out.println(requests.get(i).start+" "+requests.get(i).end+" "+requests.get(i).classroom);
        }
    }

    public static void DoSchedule(List<Request> requests)
    {
        Classroom newClassroom=new Classroom(requests.get(0).end,classrooms);
        requests.get(0).classroom=newClassroom.id;
        listOfClassrooms.add(newClassroom);

        for (int i = 1; i < requests.size(); i++) {
            Classroom currentClassroom = listOfClassrooms.poll();

            if(currentClassroom.endTime<=requests.get(i).start) {
                currentClassroom.endTime = requests.get(i).end;
                requests.get(i).classroom = currentClassroom.id;
                listOfClassrooms.add(currentClassroom);
            }else{
                listOfClassrooms.add(currentClassroom);
                classrooms++;
                newClassroom=new Classroom(requests.get(i).end,classrooms);
                requests.get(i).classroom = newClassroom.id;
                listOfClassrooms.add(newClassroom);

            }


        }
    }
    // Sort list by earliest start
    public static void sortList(ArrayList<Request> requests){
        if (requests.isEmpty()){
            //EmptyList
        }
        else{
            Collections.sort(requests, new Comparator<Request>()
                    {
                        @Override
                        public int compare(Request o1, Request o2) {
                            int comp1 = o1.start.compareTo(o2.start);
                            if (comp1 == 0)
                                return o1.end.compareTo(o2.end);
                            return comp1;
                        }
                    }
            );
        }
    }
}

class Request
{
    public Integer start=0;
    public Integer end=0;
    public int classroom=-1;

    public Request(int start, int end)
    {
        this.start=start;
        this.end = end;
    }

}

class Classroom{
    public Integer endTime;
    public Integer id;

    public Classroom(Integer newEndTime, Integer id){
        this.id =id;
        this.endTime=newEndTime;
    }

}
