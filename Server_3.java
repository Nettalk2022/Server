import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server_3 {
   // public static WorkTest wt;
   // public static First_frame ff;

   ServerSocket ssc;
   Socket sk;
   Vector <PlayThread> v = new Vector <PlayThread>();//������ ����
   Vector <String> nameV = new Vector <String>(); //������ ���� �̸� ����
   StringBuffer sb = new StringBuffer();

   // public static String ip;
   // public static String str;
   // public static String name;

   public void serverStart() { // ��������
      try {
         ssc = new ServerSocket(8000);
         System.out.println("Server START!");
         while (true) {
            sk = ssc.accept();
           System.out.println(sk.getInetAddress()+"���� ���");
            PlayThread pt = new PlayThread(this,sk);
            addThread(pt);
            pt.start();
         }
      } catch (Exception ex) {System.out.println("������������");}
   }// end

   public void addThread(PlayThread pt){
      v.addElement(pt); //vector�� PlayThread �߰� 
   }//end
   
   public void removeThread(PlayThread pt){
      v.removeElement(pt); //vector�� PlayThread ���� 
   }//end
   
   public void broadCasting(String st){ //�� client�� vector�Ѱ��ֱ�
      for(int i=0;i<v.size();i++){
         PlayThread pt = (PlayThread)v.elementAt(i);
         pt.sendMsg(st);//���⼭ ���ϴ� st �� �޽����ΰ�?
      }
   }//end

//--------�̹� ������ �ִ� client���� �̸� ������--------
 public void broadCastingName(PlayThread pt){
    //���� ���Ϳ��ִ�(������ ������) �г��� ���
    for(int i=0;i<nameV.size();i++){
       sb.append("/f");
       sb.append(nameV.get(i));
       pt.sendMsg(sb.toString());
       sb.setLength(0);//���� �ʱ�ȭ
    }
 }
 public void addNameVector(String st){
    nameV.add(st);
 }
 
 public void removeCastingName(PlayThread pt){
    for(int i=0;i<nameV.size();i++){
       sb.append("/e");
       sb.append(nameV.get(i));
       pt.sendMsg(sb.toString());
       sb.setLength(0);
    }
 }
 public void removeNameVector(String st){
    nameV.remove(st);
 }
//----------------------------------------------
   
   public static void main(String[] args) {
      Server_3 ser = new Server_3();
      ser.serverStart();
   }// main end
   
   public class PlayThread extends Thread {

       Socket sk;
       BufferedReader in;
       OutputStream out;
       Server_3 s3;
       String str;
       String name;

      public PlayThread(Server_3 s3,Socket s) {         
         this.s3 = s3;
         this.sk = s;      //Ŭ���̾�Ʈ����   
      }// ������ end

      public void run() {

         try {
            InputStreamReader isr = new InputStreamReader(sk.getInputStream());
            in = new BufferedReader(isr);
            out = sk.getOutputStream();
            PrintWriter pw = new PrintWriter(sk.getOutputStream(),true);
            name = in.readLine();
            
            s3.broadCasting("/f"+name);
            s3.broadCastingName(this);
            s3.addNameVector(name);
            
            s3.broadCasting("["+name+"]"+"���� �����ϼ̽��ϴ�.");

            while(true){
               str = in.readLine();
               System.out.println(str);
               if(str==null) return;
               if(str.charAt(1)=='s'){// "/srname-����"
                  String rname=str.substring(2,str.indexOf('-')).trim();//�ӼӸ� ������� �̸�
                  for(int i=0; i<v.size(); i++){
                     PlayThread pt = (PlayThread)v.elementAt(i);
                     if(rname.equals(pt.name)){
                        pt.sendMsg(name+"���� �ӼӸ� ���� "+str.substring(str.indexOf('-')+1));
                        break;
                     }
                  }
               }else if (str.charAt(1)=='a'){//�ѱ��ڸ� �������� �����߻������� "/a����" ���·� �������Ͽ� ���� 
                  s3.broadCasting(""+name+"���� ��: "+str.substring(2));
               }
               else {}
            }//while end
         }catch(Exception ex){
            s3.broadCasting("/e"+name);
            s3.removeCastingName(this);
            s3.removeNameVector(name);
            s3.broadCasting("["+name+"]"+"���� �����ϼ̽��ϴ�.");
            s3.removeThread(this);
            System.out.println(sk.getInetAddress()+"�� ����");
         }
      } //run end
      
       public void sendMsg(String st){
          try{
          PrintWriter pw = new PrintWriter(sk.getOutputStream(),true);
          pw.println(st);
          }catch(Exception ex){}
       }//end
      
   }// ����Ŭ���� end


}// class END