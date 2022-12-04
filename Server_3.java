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
   Vector <PlayThread> v = new Vector <PlayThread>();//스레드 벡터
   Vector <String> nameV = new Vector <String>(); //접속한 유저 이름 벡터
   StringBuffer sb = new StringBuffer();

   // public static String ip;
   // public static String str;
   // public static String name;

   public void serverStart() { // 서버시작
      try {
         ssc = new ServerSocket(8000);
         System.out.println("Server START!");
         while (true) {
            sk = ssc.accept();
           System.out.println(sk.getInetAddress()+"님이 들옴");
            PlayThread pt = new PlayThread(this,sk);
            addThread(pt);
            pt.start();
         }
      } catch (Exception ex) {System.out.println("서버연결종료");}
   }// end

   public void addThread(PlayThread pt){
      v.addElement(pt); //vector에 PlayThread 추가 
   }//end
   
   public void removeThread(PlayThread pt){
      v.removeElement(pt); //vector에 PlayThread 삭제 
   }//end
   
   public void broadCasting(String st){ //각 client에 vector넘겨주기
      for(int i=0;i<v.size();i++){
         PlayThread pt = (PlayThread)v.elementAt(i);
         pt.sendMsg(st);//여기서 말하는 st 가 메시지인가?
      }
   }//end

//--------이미 접속해 있는 client에게 이름 보내기--------
 public void broadCastingName(PlayThread pt){
    //현재 벡터에있는(접속한 유저명) 닉네임 출력
    for(int i=0;i<nameV.size();i++){
       sb.append("/f");
       sb.append(nameV.get(i));
       pt.sendMsg(sb.toString());
       sb.setLength(0);//버퍼 초기화
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
         this.sk = s;      //클라이언트소켓   
      }// 생성자 end

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
            
            s3.broadCasting("["+name+"]"+"님이 접속하셨습니다.");

            while(true){
               str = in.readLine();
               System.out.println(str);
               if(str==null) return;
               if(str.charAt(1)=='s'){// "/srname-내용"
                  String rname=str.substring(2,str.indexOf('-')).trim();//귓속말 받을사람 이름
                  for(int i=0; i<v.size(); i++){
                     PlayThread pt = (PlayThread)v.elementAt(i);
                     if(rname.equals(pt.name)){
                        pt.sendMsg(name+"님의 귓속말 ▶▶ "+str.substring(str.indexOf('-')+1));
                        break;
                     }
                  }
               }else if (str.charAt(1)=='a'){//한글자만 보냈을때 에러발생함으로 "/a내용" 형태로 보내게하여 방지 
                  s3.broadCasting(""+name+"님의 말: "+str.substring(2));
               }
               else {}
            }//while end
         }catch(Exception ex){
            s3.broadCasting("/e"+name);
            s3.removeCastingName(this);
            s3.removeNameVector(name);
            s3.broadCasting("["+name+"]"+"님이 퇴장하셨습니다.");
            s3.removeThread(this);
            System.out.println(sk.getInetAddress()+"님 퇴장");
         }
      } //run end
      
       public void sendMsg(String st){
          try{
          PrintWriter pw = new PrintWriter(sk.getOutputStream(),true);
          pw.println(st);
          }catch(Exception ex){}
       }//end
      
   }// 내부클래스 end


}// class END