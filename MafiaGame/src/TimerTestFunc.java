import java.util.Timer;
import java.util.TimerTask;

public class TimerTestFunc {
    
	public static void main(String[] args){
		Timer m_timer=new Timer();
		TimerTask m_task=new TimerTask(){
			public void run(){ //������ ������ �ð� �ڿ� ���� �� �κ�
				System.out.println("Morph");
			}
		};
		m_timer.schedule(m_task,5000); //5�ʷ� ���� ������ ����
	}
}