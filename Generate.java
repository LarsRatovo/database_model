import java.util.Calendar;
import java.util.Random;

public class Generate {
    public static void main(String[] args) {
        Random rand=new Random()
        Calendar calendar=Calendar.getInstance();
        for(int i=0i<1000000;i++){
            calendar.add(Calendar.MILLISECOND, i)
            System.out.println("INSERT INTO"" person(name,age,birth,weight) VALUES ('person"+i+",',"+rand.nextInt(100)+",'"+calendar.get(Calendar.YEAR)+"-"+(calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DAY_OF_MONTH)+"',"+rand.nextDouble()+");");
        }
    }
}
