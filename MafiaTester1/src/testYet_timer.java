/*������ test_timer_func*/
import java.util.Timer;
import java.util.TimerTask;
import java.awt.Toolkit;

public class testYet_timer {
    
	public static void main(String[] args){
		Timer m_timer=new Timer();
		TimerTask m_task=new TimerTask(){
			public void run(){ //������ ������ �ð� �ڿ� ���� �� �κ�
				Toolkit.getDefaultToolkit().beep(); //�˶�, beep
				// vote();
			}
		};
		m_timer.schedule(m_task,5000); //5�ʷ� ���� ������ ����
	}
}
